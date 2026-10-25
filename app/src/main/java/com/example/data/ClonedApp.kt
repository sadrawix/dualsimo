package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cloned_apps")
data class ClonedApp(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val customLabel: String = "",
    val cloneNumber: Int = 1,
    val isLocked: Boolean = false,
    val notificationBadgeCount: Int = 0,
    val sandboxDirectory: String = "",
    val cacheSizeMb: Double = 12.4,
    val lastLaunchedTimestamp: Long = System.currentTimeMillis(),
    val accountName: String = "Account 2"
)
