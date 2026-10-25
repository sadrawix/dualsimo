package com.example.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ClonedApp
import com.example.ui.components.AddAppBottomSheet
import com.example.ui.components.AppIconView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DualSpaceHomeScreen(
    viewModel: DualSpaceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clonedApps by viewModel.clonedApps.collectAsState()
    val availableApps by viewModel.availableApps.collectAsState()
    val selectedAppIds by viewModel.selectedAppsToClone.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val showAddModal by viewModel.showAddAppModal.collectAsState()
    val showVipModal by viewModel.showVipModal.collectAsState()
    val showSpeedModal by viewModel.showSpeedBoostModal.collectAsState()
    val showSettingsModal by viewModel.showSettingsModal.collectAsState()
    val isBoosting by viewModel.isBoosting.collectAsState()
    val isCloning by viewModel.cloningInProgress.collectAsState()
    val selectedCloneForMenu by viewModel.selectedCloneForMenu.collectAsState()

    var showRenameDialog by remember { mutableStateOf<ClonedApp?>(null) }
    var renameInputText by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070B19))
    ) {
        // Deep Cosmic Nebula Background matching Screenshot 1
        Image(
            painter = painterResource(id = R.drawable.space_bg_1792950641058),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Vignette overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCC050814),
                            Color(0x33050814),
                            Color(0x88050814),
                            Color(0xF0050814)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Top Bar matching Screenshot 1:
            // Left: Red basket (VIP/Store)
            // Center: "DualSpace"
            // Right: Mute/Notification circle & Share circle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Red Shopping Basket (Pro/VIP) icon with VIP tag
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.openVipModal() }
                        .testTag("vip_store_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFFFF416C), Color(0xFFFF4B2B))
                                ),
                                RoundedCornerShape(10.dp)
                            )
                            .shadow(6.dp, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBasket,
                            contentDescription = "VIP Store",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Little "VIP" badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-2).dp)
                            .background(Color(0xFFFFD700), RoundedCornerShape(4.dp))
                            .padding(horizontal = 3.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "VIP",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }

                // DualSpace Header Title matching Screenshot 1
                Text(
                    text = "DualSpace",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.testTag("dualspace_title")
                )

                // Right action buttons (Mute/DND, Share, Speed Rocket)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Speed Boost Rocket
                    IconButton(
                        onClick = { viewModel.runSpeedBoost() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .testTag("speed_boost_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = "Speed Boost",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Notification/Mute circle button matching Screenshot 1
                    IconButton(
                        onClick = { viewModel.toggleDnd() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .testTag("notification_dnd_button")
                    ) {
                        Icon(
                            imageVector = if (settings.isDndEnabled) Icons.Default.NotificationsOff else Icons.Default.Notifications,
                            contentDescription = "Mute / Notifications",
                            tint = if (settings.isDndEnabled) Color(0xFFFF5252) else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Share circle button matching Screenshot 1
                    IconButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "I am using DualSpace to run dual accounts of my apps! Check it out."
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share DualSpace"))
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .testTag("share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Cloned Apps Grid matching Screenshot 1:
            // Displays real cloned apps with real icons from user's phone + frosted translucent "+" square button
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Real Cloned Apps from user's phone
                items(clonedApps, key = { it.id }) { clone ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .pointerInput(clone.id) {
                                detectTapGestures(
                                    onTap = { viewModel.launchClone(clone) },
                                    onLongPress = { viewModel.openCloneMenu(clone) }
                                )
                            }
                            .padding(4.dp)
                            .testTag("clone_item_${clone.packageName}")
                    ) {
                        // 100% Real app icon from phone
                        AppIconView(
                            size = 58.dp,
                            packageName = clone.packageName,
                            cloneBadgeNumber = clone.cloneNumber,
                            isLocked = clone.isLocked,
                            showNotificationBadge = clone.notificationBadgeCount > 0,
                            notificationCount = clone.notificationBadgeCount
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = clone.customLabel.ifEmpty { clone.appName },
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Frosted translucent "+" square button matching Screenshot 1
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { viewModel.openAddAppModal() }
                            .padding(4.dp)
                            .testTag("add_app_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.16f))
                                .border(
                                    width = 1.2.dp,
                                    color = Color.White.copy(alpha = pulseGlow),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add App to DualSpace",
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "",
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Bottom Navigation / System Bar with Safe Insets
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .background(Color(0x990A0E1A))
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.1f), Color.Transparent)
                        ),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E5FF))
                        )
                        Text(
                            text = "Dual Space: ${clonedApps.size} Real Clones",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        IconButton(
                            onClick = { viewModel.openSettingsModal() },
                            modifier = Modifier.size(36.dp).testTag("settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "App Lock & Security",
                                tint = if (settings.isAppLockEnabled) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Add App Bottom Sheet (Screenshot 2)
        if (showAddModal) {
            AddAppBottomSheet(
                availableApps = availableApps,
                selectedAppIds = selectedAppIds,
                maxLimit = settings.maxFreeClones,
                isVip = settings.isVip,
                isCloning = isCloning,
                onToggleSelect = { viewModel.toggleAppSelection(it) },
                onConfirmClone = { viewModel.executeCloning() },
                onDismiss = { viewModel.closeAddAppModal() }
            )
        }

        // Clone Options Long-Press Sheet
        selectedCloneForMenu?.let { clone ->
            ModalBottomSheet(
                onDismissRequest = { viewModel.closeCloneMenu() },
                containerColor = Color(0xFF131B2E),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        AppIconView(
                            size = 46.dp,
                            packageName = clone.packageName,
                            cloneBadgeNumber = clone.cloneNumber
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = clone.customLabel.ifEmpty { clone.appName },
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${clone.packageName} • ${String.format("%.1f", clone.cacheSizeMb)} MB",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Open Real App
                    CloneMenuRow(
                        icon = Icons.Default.Launch,
                        title = "Launch Real App",
                        subtitle = "Open this app instance on your phone",
                        color = Color(0xFF00E5FF),
                        onClick = {
                            viewModel.closeCloneMenu()
                            viewModel.launchClone(clone)
                        }
                    )

                    // System Details & Storage
                    CloneMenuRow(
                        icon = Icons.Default.Info,
                        title = "App Info & Permissions",
                        subtitle = "Manage phone system permissions and storage",
                        onClick = {
                            viewModel.closeCloneMenu()
                            val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", clone.packageName, null)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(settingsIntent)
                        }
                    )

                    // Rename
                    CloneMenuRow(
                        icon = Icons.Default.Edit,
                        title = "Rename Clone",
                        subtitle = "Customize icon label in DualSpace",
                        onClick = {
                            renameInputText = clone.customLabel.ifEmpty { clone.appName }
                            showRenameDialog = clone
                            viewModel.closeCloneMenu()
                        }
                    )

                    // Clear Cache
                    CloneMenuRow(
                        icon = Icons.Default.Storage,
                        title = "Clear Cache",
                        subtitle = "Purge ${String.format("%.1f", clone.cacheSizeMb)} MB temporary data",
                        onClick = { viewModel.clearCloneCache(clone) }
                    )

                    // Lock
                    CloneMenuRow(
                        icon = if (clone.isLocked) Icons.Default.LockOpen else Icons.Default.Lock,
                        title = if (clone.isLocked) "Unlock App" else "Lock with Privacy PIN",
                        subtitle = "Protect this clone from unauthorized access",
                        onClick = { viewModel.toggleCloneLock(clone) }
                    )

                    // Delete
                    CloneMenuRow(
                        icon = Icons.Default.Delete,
                        title = "Delete Clone",
                        subtitle = "Remove this cloned instance from DualSpace",
                        color = Color(0xFFFF5252),
                        onClick = { viewModel.deleteClone(clone) }
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // Rename Dialog
        showRenameDialog?.let { clone ->
            AlertDialog(
                onDismissRequest = { showRenameDialog = null },
                containerColor = Color(0xFF1A2238),
                title = {
                    Text("Rename Clone", color = Color.White, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text(
                            "Enter a new name for this clone (e.g. Work, Alt, Secondary):",
                            color = Color(0xFFCBD5E1),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = renameInputText,
                            onValueChange = { renameInputText = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("rename_clone_input")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.renameClone(clone, renameInputText.trim())
                            showRenameDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077F5))
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRenameDialog = null }) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            )
        }

        // VIP / Pro Space Store Modal
        if (showVipModal) {
            VipStoreDialog(
                isVip = settings.isVip,
                onToggleVip = { viewModel.toggleVip(it) },
                onDismiss = { viewModel.closeVipModal() }
            )
        }

        // Speed Boost Clean Modal
        if (showSpeedModal) {
            SpeedBoostDialog(
                isBoosting = isBoosting,
                onDismiss = { viewModel.closeSpeedBoostModal() }
            )
        }

        // Settings & App Lock Modal
        if (showSettingsModal) {
            DualSpaceSettingsDialog(
                settings = settings,
                onToggleAppLock = { enabled, pin -> viewModel.setAppLock(enabled, pin) },
                onSetCamouflage = { viewModel.setCamouflage(it) },
                onDismiss = { viewModel.closeSettingsModal() }
            )
        }
    }
}

@Composable
fun CloneMenuRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    color: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                color = color,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = Color(0xFF94A3B8),
                fontSize = 12.sp
            )
        }
    }
}
