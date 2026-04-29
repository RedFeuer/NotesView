package com.example.noteslist.domain.useCase

import com.example.noteslist.domain.repository.AppStartupRepository
import javax.inject.Inject

class ShouldShowInitialShimmerUseCase @Inject constructor(
    private val appStartupRepository: AppStartupRepository,
) {
    suspend operator fun invoke() : Boolean {
        return appStartupRepository.shouldShowInitialShimmer()
    }
}