package com.pusher.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pusher.app.MainActivity
import com.pusher.app.R
import com.pusher.app.dataStore
import com.pusher.app.domain.Notifications
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "MyFirebaseMsgService"
class PusherFirebaseMessaging : FirebaseMessagingService() {
    companion object {
        private const val TAG = "PusherFirebaseMessaging"
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        GlobalScope.launch {
            saveFCMToken(token)
        }
    }

    private suspend fun saveFCMToken(token: String) {
        Log.d(TAG, "saving FCM token")

        val fcmTokenKey = stringPreferencesKey("fcm_token")
        baseContext.dataStore.edit {
            it[fcmTokenKey] = token
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "From: ${message.from}")

        // Check if message contains a data payload.
        if (message.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${message.data}")
        }

        // Check if message contains a notification payload.
        message.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            it.body?.let { _ ->
                GlobalScope.launch {
                    sendNotification(it)
                }
            }
        }
    }

    private suspend fun sendNotification(message: RemoteMessage.Notification) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val notificationImageBitmap = if (message.imageUrl != null) {
            getBitmapFromURL(message.imageUrl.toString())
        } else {
            null
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, Notifications.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(message.title)
            .setContentText(message.body)
            .setLargeIcon(notificationImageBitmap)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        val channel = NotificationChannel(
            Notifications.NOTIFICATION_CHANNEL_ID,
            Notifications.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        notificationManager.createNotificationChannel(channel)

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private suspend fun getBitmapFromURL(src: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        }
    }
}