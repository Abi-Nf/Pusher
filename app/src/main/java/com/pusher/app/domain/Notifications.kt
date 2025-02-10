package com.pusher.app.domain

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.ContextCompat.getSystemService

object Notifications {
    const val NOTIFICATION_CHANNEL_ID = "pusher_notification_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Pusher notification channel"
}

fun initNotificationChanel(ctx: Context) {
    // Create channel to show notifications.
    val notificationManager = getSystemService(ctx, NotificationManager::class.java)
    notificationManager?.createNotificationChannel(
        NotificationChannel(
            Notifications.NOTIFICATION_CHANNEL_ID,
            Notifications.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT,
        ),
    )
}