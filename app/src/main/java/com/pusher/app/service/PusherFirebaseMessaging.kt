package com.pusher.app.service

import com.google.firebase.messaging.FirebaseMessagingService

class PusherFirebaseMessaging : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}