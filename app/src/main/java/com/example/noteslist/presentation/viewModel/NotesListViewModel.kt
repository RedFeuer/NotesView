package com.example.noteslist.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.domain.repository.NotesRepository
import com.example.noteslist.domain.useCase.GetNotesUseCase
import com.example.noteslist.domain.useCase.ToggleNoteViewedUseCase
import com.example.noteslist.presentation.notes.toNoteListItems
import com.example.noteslist.presentation.state.NotesListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class NotesListViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val toggleNoteViewedUseCase: ToggleNoteViewedUseCase,
) : ViewModel() {
    /** раскрытые стеки */
    private val expandedStackIds = MutableStateFlow<Set<String>>(emptySet())

    /** состояние экрана в данный момент
     * получается из полученных из репозитория заметок и ID раскрытых стеков заметок */
    val uiState : StateFlow<NotesListUiState> = combine(
        getNotesUseCase(),
        expandedStackIds
    ) { notes, expandedStackIds ->
        NotesListUiState(
            items = notes.toNoteListItems(),
            expandedStackIds = expandedStackIds,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NotesListUiState(),
    )

    /** обработка длинного нажатия */
    fun onNoteLongClick(noteUiId : String) {
        toggleNoteViewedUseCase(noteUiId)
    }

    /** меняем expandedStackIds */
    fun onStackExpandedChange(stackId: String, isExpanded: Boolean) {
        expandedStackIds.update { oldIds ->
            if (isExpanded) {
                oldIds + stackId
            } else {
                oldIds - stackId
            }
        }
    }
}