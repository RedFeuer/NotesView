package com.example.noteslist.data.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

private const val APP_SETTING_DATA_STORE_NAME = "app_settings"

val Context.appSettingsDataStore by preferencesDataStore(
    name = APP_SETTING_DATA_STORE_NAME
)