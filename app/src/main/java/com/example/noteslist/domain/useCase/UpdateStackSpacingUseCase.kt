package com.example.noteslist.domain.useCase

import com.example.noteslist.di.coroutine.qualifier.ApplicationCoroutineScope
import com.example.noteslist.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class UpdateStackSpacingUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    @ApplicationCoroutineScope private val applicationCoroutineScope: CoroutineScope,
) {
    operator fun invoke(spacingDp : Int) : Job {
        return applicationCoroutineScope.launch {
            settingsRepository.updateStackSpacing(spacingDp)
        }
    }
}