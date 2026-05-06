package com.example.noteslist.domain.repository

import com.example.noteslist.domain.settings.StackSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeStackSettings() : Flow<StackSettings>

    suspend fun updateStackSpacing(spacingDp : Int)

    suspend fun updateStackMaxVisible(count : Int)
}