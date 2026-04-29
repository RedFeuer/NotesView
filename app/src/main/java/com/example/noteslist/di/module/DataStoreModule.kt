package com.example.noteslist.di.module

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.noteslist.data.datastore.appSettingsDataStore
import com.example.noteslist.di.scope.AppScope
import dagger.Module
import dagger.Provides

@Module
object DataStoreModule {
    @Provides
    @AppScope
    fun providePreferencesDataStore(application: Application) : DataStore<Preferences> {
        return application.appSettingsDataStore
    }
}