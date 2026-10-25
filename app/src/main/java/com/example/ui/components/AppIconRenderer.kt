package com.example.ui.components

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun AppIconView(
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    packageName: String? = null,
    drawable: Drawable? = null,
    cloneBadgeNumber: Int? = null,
    isLocked: Boolean = false,
    showNotificationBadge: Boolean = false,
    notificationCount: Int = 1
) {
    val context = LocalContext.current

    // Obtain the 100% real system icon from the device
    val realDrawable = remember(packageName, drawable) {
        if (drawable != null) {
            drawable
        } else if (!packageName.isNullOrEmpty()) {
            try {
                context.packageManager.getApplicationIcon(packageName)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Native Real Icon from the User's Phone
        if (realDrawable != null) {
            AndroidView(
                factory = { ctx ->
                    ImageView(ctx).apply {
                        scaleType = ImageView.ScaleType.FIT_CENTER
                        setImageDrawable(realDrawable)
                    }
                },
                update = { imageView ->
                    imageView.setImageDrawable(realDrawable)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(size * 0.22f))
            )
        } else {
            // Fallback system android robot
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(size * 0.22f))
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = null,
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(size * 0.55f)
                )
            }
        }

        // Notification red dot / badge '1' on top-right (matching DualSpace UI)
        if (showNotificationBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 3.dp, y = (-3).dp)
                    .size(size * 0.32f)
                    .background(Color(0xFFFF334B), CircleShape)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$notificationCount",
                    color = Color.White,
                    fontSize = (size.value * 0.16f).sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Clone Tag Badge (e.g. '2nd' or clone indicator)
        if (cloneBadgeNumber != null && cloneBadgeNumber > 0 && !showNotificationBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .background(Color(0xFF00E5FF), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "2nd",
                    color = Color(0xFF0B0E17),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Privacy Lock Badge
        if (isLocked) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-2).dp, y = 2.dp)
                    .size(16.dp)
                    .background(Color(0xFFF59E0B), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color.Black,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
    }
}
