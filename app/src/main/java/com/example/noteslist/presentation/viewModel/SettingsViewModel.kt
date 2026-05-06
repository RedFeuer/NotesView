package com.example.noteslist.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.domain.useCase.ObserveStackSettingsUseCase
import com.example.noteslist.domain.useCase.UpdateStackMaxVisibleUseCase
import com.example.noteslist.domain.useCase.UpdateStackSpacingUseCase
import com.example.noteslist.presentation.state.SettingsUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    observeStackSettingsUseCase: ObserveStackSettingsUseCase,
    private val updateStackSpacingUseCase: UpdateStackSpacingUseCase,
    private val updateStackMaxVisibleUseCase: UpdateStackMaxVisibleUseCase,
) : ViewModel() {
    /** наблюдаемое состояние экрана настроек */
    val uiState : StateFlow<SettingsUiState> = observeStackSettingsUseCase()
        .map { stackSettings ->
            SettingsUiState(
                stackSpacingDp = stackSettings.stackSpacingDp,
                stackMaxVisible = stackSettings.stackMaxVisible
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(),
        )

    fun onStackSpacingChanged(newSpacing : Int) {
        updateStackSpacingUseCase(newSpacing)
    }

    fun onStackMaxVisibleChanged(newCount : Int) {
        updateStackMaxVisibleUseCase(newCount)
    }
}