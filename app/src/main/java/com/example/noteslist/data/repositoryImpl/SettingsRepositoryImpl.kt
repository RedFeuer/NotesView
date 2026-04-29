package com.example.noteslist.data.repositoryImpl

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.noteslist.domain.repository.SettingsRepository
import com.example.noteslist.domain.settings.StackSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore : DataStore<Preferences>,
) : SettingsRepository {
    override fun observeStackSettings(): Flow<StackSettings> {
        return dataStore.data
            .catch { error ->
                if (error is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw error
                }
            }
            .map { preferences ->
                StackSettings(
                    stackSpacingDp = preferences[Keys.STACK_SPACING_DP]
                        ?: StackSettings.DEFAULT_STACK_SPACING_VERTICAL_DP,
                    stackMaxVisible = preferences[Keys.STACK_MAX_VISIBLE]
                        ?: StackSettings.DEFAULT_STACK_MAX_VISIBLE
                )
            }
            .distinctUntilChanged()
    }

    override suspend fun updateStackSpacing(spacingDp: Int) {
        val safeValue = spacingDp.coerceIn(
            minimumValue = StackSettings.MIN_STACK_SPACING_DP,
            maximumValue = StackSettings.MAX_STACK_SPACING_DP
        )

        dataStore.edit { preferences ->
            preferences[Keys.STACK_SPACING_DP] = safeValue
        }
    }

    override suspend fun updateStackMaxVisible(count: Int) {
        val safeValue = count.coerceIn(
            minimumValue = StackSettings.MIN_STACK_MAX_VISIBLE,
            maximumValue = StackSettings.MAX_STACK_MAX_VISIBLE
        )

        dataStore.edit { preferences ->
            preferences[Keys.STACK_MAX_VISIBLE] = safeValue
        }
    }
}

/** для обращения к preferences */
private object Keys {
    val STACK_SPACING_DP = intPreferencesKey("stack_spacing_dp")
    val STACK_MAX_VISIBLE = intPreferencesKey("stack_max_visible")
}