package com.example.data

import android.graphics.drawable.Drawable

data class CloneableApp(
    val id: String,
    val packageName: String,
    val name: String,
    val isSystemApp: Boolean = false,
    val versionName: String = "1.0",
    val drawableIcon: Drawable? = null
)

data class DualSpaceSettings(
    val isVip: Boolean = false,
    val isDndEnabled: Boolean = false,
    val isAppLockEnabled: Boolean = false,
    val pinCode: String = "",
    val camouflageApp: String = "DualSpace", // "DualSpace", "Calculator", "Weather"
    val maxFreeClones: Int = 3
)
