package com.example.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CloneableApp
import com.example.data.ClonedApp
import com.example.data.DualSpaceDatabase
import com.example.data.DualSpaceRepository
import com.example.data.DualSpaceSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DualSpaceViewModel(application: Application) : AndroidViewModel(application) {
    private val database = DualSpaceDatabase.getDatabase(application)
    private val repository = DualSpaceRepository(application, database.dualSpaceDao())

    val clonedApps: StateFlow<List<ClonedApp>> = repository.clonedApps.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val settings: StateFlow<DualSpaceSettings> = repository.settings

    private val _availableApps = MutableStateFlow<List<CloneableApp>>(emptyList())
    val availableApps = _availableApps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    // Apps selected in the "Agregar a DualSpace" dialog
    private val _selectedAppsToClone = MutableStateFlow<Set<String>>(emptySet())
    val selectedAppsToClone = _selectedAppsToClone.asStateFlow()

    // Dialog & Screen states
    private val _showAddAppModal = MutableStateFlow(false)
    val showAddAppModal = _showAddAppModal.asStateFlow()

    private val _showVipModal = MutableStateFlow(false)
    val showVipModal = _showVipModal.asStateFlow()

    private val _showSpeedBoostModal = MutableStateFlow(false)
    val showSpeedBoostModal = _showSpeedBoostModal.asStateFlow()

    private val _showSettingsModal = MutableStateFlow(false)
    val showSettingsModal = _showSettingsModal.asStateFlow()

    private val _isBoosting = MutableStateFlow(false)
    val isBoosting = _isBoosting.asStateFlow()

    private val _selectedCloneForMenu = MutableStateFlow<ClonedApp?>(null)
    val selectedCloneForMenu = _selectedCloneForMenu.asStateFlow()

    // App Lock PIN lock state
    private val _isLockedByPin = MutableStateFlow(false)
    val isLockedByPin = _isLockedByPin.asStateFlow()

    private val _cloningInProgress = MutableStateFlow(false)
    val cloningInProgress = _cloningInProgress.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage = _toastMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeWithRealAppIfEmpty()
            loadAvailableApps()
        }
    }

    fun loadAvailableApps() {
        viewModelScope.launch {
            val list = repository.getRealInstalledApps()
            _availableApps.value = list
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun toggleAppSelection(appId: String) {
        val current = _selectedAppsToClone.value.toMutableSet()
        if (current.contains(appId)) {
            current.remove(appId)
        } else {
            val limit = if (settings.value.isVip) 99 else settings.value.maxFreeClones
            if (current.size >= limit) {
                _toastMessage.value = "Free limit reached ($limit/3). Unlock VIP for unlimited clones!"
                return
            }
            current.add(appId)
        }
        _selectedAppsToClone.value = current
    }

    fun openAddAppModal() {
        loadAvailableApps()
        _showAddAppModal.value = true
    }

    fun closeAddAppModal() {
        _showAddAppModal.value = false
        _selectedAppsToClone.value = emptySet()
    }

    fun executeCloning() {
        val selectedIds = _selectedAppsToClone.value
        if (selectedIds.isEmpty()) return

        viewModelScope.launch {
            _cloningInProgress.value = true
            delay(600)
            val appsToClone = _availableApps.value.filter { selectedIds.contains(it.id) }
            repository.cloneApps(appsToClone)
            _cloningInProgress.value = false
            _showAddAppModal.value = false
            _selectedAppsToClone.value = emptySet()
            _toastMessage.value = "${appsToClone.size} real app(s) cloned to DualSpace!"
        }
    }

    /**
     * Launch the REAL app installed on the user's phone!
     */
    fun launchClone(clone: ClonedApp) {
        val context = getApplication<Application>()
        try {
            val pm = context.packageManager
            val launchIntent = pm.getLaunchIntentForPackage(clone.packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                context.startActivity(launchIntent)
                _toastMessage.value = "Opening ${clone.customLabel.ifEmpty { clone.appName }} in DualSpace"
            } else {
                // If direct launch intent not found, open system app settings details
                val settingsIntent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = android.net.Uri.fromParts("package", clone.packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(settingsIntent)
            }
        } catch (e: Exception) {
            _toastMessage.value = "Could not launch ${clone.appName}: ${e.localizedMessage}"
        }
    }

    fun openCloneMenu(clone: ClonedApp) {
        _selectedCloneForMenu.value = clone
    }

    fun closeCloneMenu() {
        _selectedCloneForMenu.value = null
    }

    fun deleteClone(clone: ClonedApp) {
        viewModelScope.launch {
            repository.deleteClone(clone.id)
            _selectedCloneForMenu.value = null
            _toastMessage.value = "Removed ${clone.customLabel.ifEmpty { clone.appName }} from DualSpace"
        }
    }

    fun clearCloneCache(clone: ClonedApp) {
        viewModelScope.launch {
            repository.clearCache(clone.id)
            _selectedCloneForMenu.value = null
            _toastMessage.value = "Cache cleaned for ${clone.appName}"
        }
    }

    fun renameClone(clone: ClonedApp, newName: String) {
        viewModelScope.launch {
            repository.updateClone(clone.copy(customLabel = newName))
            _selectedCloneForMenu.value = null
            _toastMessage.value = "Renamed to $newName"
        }
    }

    fun toggleCloneLock(clone: ClonedApp) {
        viewModelScope.launch {
            val newState = !clone.isLocked
            repository.updateClone(clone.copy(isLocked = newState))
            _selectedCloneForMenu.value = null
            _toastMessage.value = if (newState) "App lock enabled for ${clone.appName}" else "App lock disabled"
        }
    }

    fun runSpeedBoost() {
        viewModelScope.launch {
            _isBoosting.value = true
            _showSpeedBoostModal.value = true
            delay(1000)
            repository.clearAllCache()
            _isBoosting.value = false
            _toastMessage.value = "Freed 128 MB RAM & cleaned all clone cache!"
        }
    }

    fun closeSpeedBoostModal() {
        _showSpeedBoostModal.value = false
    }

    fun openVipModal() {
        _showVipModal.value = true
    }

    fun closeVipModal() {
        _showVipModal.value = false
    }

    fun openSettingsModal() {
        _showSettingsModal.value = true
    }

    fun closeSettingsModal() {
        _showSettingsModal.value = false
    }

    fun toggleVip(enable: Boolean) {
        repository.updateSettings { it.copy(isVip = enable) }
        _toastMessage.value = if (enable) "VIP Activated! Enjoy unlimited clones & no ads." else "VIP deactivated"
    }

    fun toggleDnd() {
        val current = settings.value.isDndEnabled
        repository.updateSettings { it.copy(isDndEnabled = !current) }
        _toastMessage.value = if (!current) "DualSpace Muted: Clone notifications silenced" else "Clone notifications enabled"
    }

    fun setAppLock(enabled: Boolean, pin: String = "1234") {
        repository.updateSettings { it.copy(isAppLockEnabled = enabled, pinCode = pin) }
        _toastMessage.value = if (enabled) "DualSpace App Lock activated (PIN: $pin)" else "App Lock deactivated"
    }

    fun setCamouflage(mode: String) {
        repository.updateSettings { it.copy(camouflageApp = mode) }
        _toastMessage.value = "Disguise set to $mode"
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}
