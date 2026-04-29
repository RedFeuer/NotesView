package com.example.noteslist.presentation.state

import com.example.noteslist.domain.settings.StackSettings

data class SettingsUiState(
    val stackSpacingDp : Int = StackSettings.DEFAULT_STACK_SPACING_VERTICAL_DP,
    val stackMaxVisible : Int = StackSettings.DEFAULT_STACK_MAX_VISIBLE,
)
