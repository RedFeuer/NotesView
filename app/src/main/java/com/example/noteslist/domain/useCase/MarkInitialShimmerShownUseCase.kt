package com.example.noteslist.domain.useCase

import com.example.noteslist.di.coroutine.qualifier.ApplicationCoroutineScope
import com.example.noteslist.domain.repository.AppStartupRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class MarkInitialShimmerShownUseCase @Inject constructor(
    private val appStartupRepository: AppStartupRepository,
    @ApplicationCoroutineScope private val applicationCoroutineScope: CoroutineScope,
) {
    suspend operator fun invoke() : Job {
        return applicationCoroutineScope.launch {
            appStartupRepository.markInitialShimmerShown()
        }
    }
}