package com.example.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class DualSpaceRepository(
    private val context: Context,
    private val dao: DualSpaceDao
) {
    val clonedApps: Flow<List<ClonedApp>> = dao.getAllClonedApps()
    val clonedAppsCount: Flow<Int> = dao.getClonedAppsCount()

    private val _settings = MutableStateFlow(DualSpaceSettings())
    val settings = _settings.asStateFlow()

    fun updateSettings(transform: (DualSpaceSettings) -> DualSpaceSettings) {
        _settings.value = transform(_settings.value)
    }

    /**
     * Query all REAL apps installed on the user's phone.
     */
    suspend fun getRealInstalledApps(): List<CloneableApp> = withContext(Dispatchers.IO) {
        val result = mutableListOf<CloneableApp>()
        val seenPackages = mutableSetOf<String>()
        val pm = context.packageManager

        try {
            // Query launchable applications
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(intent, 0)

            for (info in resolveInfos) {
                val pkgName = info.activityInfo.packageName
                // Skip our own app
                if (pkgName == context.packageName) continue
                if (seenPackages.contains(pkgName)) continue
                seenPackages.add(pkgName)

                val appName = try {
                    info.loadLabel(pm).toString()
                } catch (e: Exception) {
                    info.activityInfo.name
                }

                val icon = try {
                    info.loadIcon(pm)
                } catch (e: Exception) {
                    pm.defaultActivityIcon
                }

                val isSystem = try {
                    val appInfo = pm.getApplicationInfo(pkgName, 0)
                    (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                } catch (e: Exception) {
                    false
                }

                result.add(
                    CloneableApp(
                        id = pkgName,
                        packageName = pkgName,
                        name = appName,
                        isSystemApp = isSystem,
                        drawableIcon = icon
                    )
                )
            }

            // Also check getInstalledApplications for any other user-installed apps
            val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            for (appInfo in installedApps) {
                val pkgName = appInfo.packageName
                if (pkgName == context.packageName) continue
                if (seenPackages.contains(pkgName)) continue

                // Only include if has launch intent
                if (pm.getLaunchIntentForPackage(pkgName) != null) {
                    seenPackages.add(pkgName)
                    val appName = pm.getApplicationLabel(appInfo).toString()
                    val icon = pm.getApplicationIcon(appInfo)
                    val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                    result.add(
                        CloneableApp(
                            id = pkgName,
                            packageName = pkgName,
                            name = appName,
                            isSystemApp = isSystem,
                            drawableIcon = icon
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Sort so non-system user-installed apps appear first, then alphabetically
        result.sortedWith(
            compareBy<CloneableApp> { it.isSystemApp }
                .thenBy { it.name.lowercase() }
        )
    }

    /**
     * Get the real Drawable icon of any package installed on this device.
     */
    fun getRealAppIcon(packageName: String): Drawable? {
        return try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Initialize first-time real apps if database is empty.
     */
    suspend fun initializeWithRealAppIfEmpty() = withContext(Dispatchers.IO) {
        val count = clonedAppsCount.firstOrNull() ?: 0
        if (count == 0) {
            val realApps = getRealInstalledApps()
            // Pick the first real launchable user or non-system app, or Chrome / first available
            val appToClone = realApps.firstOrNull { !it.isSystemApp } ?: realApps.firstOrNull()
            if (appToClone != null) {
                dao.insertClonedApp(
                    ClonedApp(
                        packageName = appToClone.packageName,
                        appName = appToClone.name,
                        customLabel = appToClone.name,
                        cloneNumber = 1,
                        isLocked = false,
                        notificationBadgeCount = 1,
                        sandboxDirectory = "/dualspace/sandboxes/${appToClone.packageName}_clone_1",
                        cacheSizeMb = 18.2,
                        accountName = "Account 2"
                    )
                )
            }
        }
    }

    suspend fun cloneApps(selectedApps: List<CloneableApp>) = withContext(Dispatchers.IO) {
        for (app in selectedApps) {
            val count = 1
            val sandbox = "/dualspace/sandboxes/${app.packageName}_clone_$count"
            dao.insertClonedApp(
                ClonedApp(
                    packageName = app.packageName,
                    appName = app.name,
                    customLabel = app.name,
                    cloneNumber = count,
                    isLocked = false,
                    notificationBadgeCount = 0,
                    sandboxDirectory = sandbox,
                    cacheSizeMb = 12.0 + (app.name.hashCode() % 10).coerceAtLeast(1) * 1.5,
                    accountName = "Account 2"
                )
            )
        }
    }

    suspend fun deleteClone(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteById(id)
    }

    suspend fun clearCache(id: Long) = withContext(Dispatchers.IO) {
        dao.clearCache(id)
    }

    suspend fun clearAllCache() = withContext(Dispatchers.IO) {
        dao.clearAllCache()
    }

    suspend fun updateClone(app: ClonedApp) = withContext(Dispatchers.IO) {
        dao.updateClonedApp(app)
    }
}
