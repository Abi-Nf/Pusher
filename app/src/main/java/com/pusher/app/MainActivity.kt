package com.pusher.app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.getSystemService
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.pusher.app.ui.theme.PusherTheme

const val TAG = "PusherMainActivity"
const val NOTIFICATION_CHANNEL_ID = "pusher_notification_channel"
const val NOTIFICATION_CHANNEL_NAME = "Pusher notification channel"

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PusherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val context = LocalContext.current
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val notificationPermissionState =
                            rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
                        when {
                            notificationPermissionState.status.isGranted -> {}
                            notificationPermissionState.status.shouldShowRationale -> {
                                Toast.makeText(
                                    context,
                                    "Please grant notifications if you want to receive messages from FCM",
                                    Toast.LENGTH_LONG
                                ).show();
                                LaunchPermissionRequest(notificationPermissionState)
                            }
                            else -> {
                                LaunchPermissionRequest(notificationPermissionState)
                            }
                        }
                    }

                    initNotificationChanel(context)

                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

fun initNotificationChanel(ctx: Context) {
    // Create channel to show notifications.
    val channelId = NOTIFICATION_CHANNEL_ID
    val channelName = NOTIFICATION_CHANNEL_NAME
    val notificationManager = getSystemService(ctx, NotificationManager::class.java)
    notificationManager?.createNotificationChannel(
        NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT,
        ),
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LaunchPermissionRequest(permissionState: PermissionState) {
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PusherTheme {
        Greeting("Android")
    }
}