package com.example.noteslist.data.repositoryImpl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.noteslist.domain.repository.AppStartupRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AppStartupRepositoryImpl @Inject constructor(
    private val dataStore : DataStore<Preferences>,
) : AppStartupRepository {
    override suspend fun shouldShowInitialShimmer(): Boolean {
        val preferences = dataStore.data.first()

        return preferences[Keys.INITIAL_SHIMMER_SHOWN] != true
    }

    override suspend fun markInitialShimmerShown() {
        dataStore.edit { preferences ->
            preferences[Keys.INITIAL_SHIMMER_SHOWN] = true
        }
    }

    private object Keys {
        val INITIAL_SHIMMER_SHOWN = booleanPreferencesKey("initial_shimmer_shown")
    }
}