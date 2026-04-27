package com.example.noteslist.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.di.coroutine.qualifier.DefaultDispatcher
import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.domain.useCase.GetNotesUseCase
import com.example.noteslist.domain.useCase.ToggleNoteViewedUseCase
import com.example.noteslist.presentation.notes.toNoteListItems
import com.example.noteslist.presentation.state.NotesListUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(FlowPreview::class)
class NotesListViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val toggleNoteViewedUseCase: ToggleNoteViewedUseCase,
    @DefaultDispatcher private val defaultCoroutineDispatcher: CoroutineDispatcher,
) : ViewModel() {
    /** раскрытые стеки */
    private val expandedStackIds = MutableStateFlow<Set<String>>(emptySet())

    /** сырые поисковые запросы */
    private val rawSearchQuery = MutableStateFlow("")

    /** отфильтрованные поисковые запросы */
    private val searchQuery = rawSearchQuery
        .map { query ->
            query.trim()
        }
        .debounce(300L)
        .distinctUntilChanged()

    /** состояние экрана в данный момент
     * получается из полученных из репозитория заметок, ID раскрытых стеков заметок и конкретного поискового запроса */
    val uiState : StateFlow<NotesListUiState> = combine(
        getNotesUseCase(),
        expandedStackIds,
        searchQuery,
    ) { notes, expandedStackIds, query ->
        val filteredNotes = filterNotesByTitle(
            notes = notes,
            query = query,
        )

        NotesListUiState(
            items = filteredNotes.toNoteListItems(),
            expandedStackIds = expandedStackIds,
            searchQuery = query,
        )
    }
        .flowOn(defaultCoroutineDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NotesListUiState(),
        )

    /** Job отмечания заметки прочитанной */
    private var togglingViewedJob : Job? = null

    fun onSearchQueryChanged(newQuery : String) {
        rawSearchQuery.value = newQuery
    }

    /** обработка длинного нажатия */
    fun onNoteLongClick(noteUiId : String) {
        if (togglingViewedJob?.isActive == true) return

        togglingViewedJob = toggleNoteViewedUseCase(noteUiId)
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

    private fun filterNotesByTitle(
        notes : List<Note>,
        query : String,
    ) : List<Note> {
        if (query.isBlank()) return notes

        return notes.filter { note ->
            note.title.orEmpty().contains(
                other = query,
                ignoreCase = true, // независимо от регистра
            )
        }
    }
}