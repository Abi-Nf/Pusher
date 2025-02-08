package com.pusher.app

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "settings")
class DataStore: Application() {
}