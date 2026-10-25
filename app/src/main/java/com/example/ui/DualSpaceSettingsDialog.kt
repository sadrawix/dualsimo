package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.DualSpaceSettings

@Composable
fun DualSpaceSettingsDialog(
    settings: DualSpaceSettings,
    onToggleAppLock: (Boolean, String) -> Unit,
    onSetCamouflage: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var isLockEnabled by remember { mutableStateOf(settings.isAppLockEnabled) }
    var pinText by remember { mutableStateOf(if (settings.pinCode.isNotEmpty()) settings.pinCode else "1234") }
    var selectedCamouflage by remember { mutableStateOf(settings.camouflageApp) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xFF00E5FF).copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Privacy & Security",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // App Lock Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dual Space App Lock",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Require 4-digit PIN to open Dual Space",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = isLockEnabled,
                        onCheckedChange = { isLockEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF00C853)
                        ),
                        modifier = Modifier.testTag("app_lock_switch")
                    )
                }

                if (isLockEnabled) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = pinText,
                        onValueChange = { if (it.length <= 4) pinText = it },
                        label = { Text("4-Digit Security PIN") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("app_lock_pin_input"),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, null, tint = Color(0xFF00E5FF))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Camouflage Disguise
                Text(
                    text = "Icon Camouflage Disguise",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Disguise DualSpace on phone home screen",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CamouflageOption(
                        icon = Icons.Default.Security,
                        name = "DualSpace",
                        isSelected = selectedCamouflage == "DualSpace",
                        onClick = { selectedCamouflage = "DualSpace" },
                        modifier = Modifier.weight(1f)
                    )
                    CamouflageOption(
                        icon = Icons.Default.Calculate,
                        name = "Calculator",
                        isSelected = selectedCamouflage == "Calculator",
                        onClick = { selectedCamouflage = "Calculator" },
                        modifier = Modifier.weight(1f)
                    )
                    CamouflageOption(
                        icon = Icons.Default.Cloud,
                        name = "Weather",
                        isSelected = selectedCamouflage == "Weather",
                        onClick = { selectedCamouflage = "Weather" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onToggleAppLock(isLockEnabled, pinText)
                        onSetCamouflage(selectedCamouflage)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_settings_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077F5))
                ) {
                    Text("Save Settings", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CamouflageOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFF1E3A8A) else Color(0xFF1E293B))
            .border(
                width = 1.5.dp,
                color = if (isSelected) Color(0xFF38BDF8) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Color(0xFF38BDF8) else Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = name,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
