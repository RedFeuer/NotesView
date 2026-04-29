package com.example.noteslist.domain.useCase

import com.example.noteslist.domain.repository.SettingsRepository
import com.example.noteslist.domain.settings.StackSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveStackSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke() : Flow<StackSettings> {
        return settingsRepository.observeStackSettings()
    }
}