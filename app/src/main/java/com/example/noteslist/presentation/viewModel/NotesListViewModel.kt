package com.example.noteslist.presentation.viewModel

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.di.coroutine.qualifier.DefaultDispatcher
import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.domain.useCase.GetNotesUseCase
import com.example.noteslist.domain.useCase.MarkInitialShimmerShownUseCase
import com.example.noteslist.domain.useCase.ObserveStackSettingsUseCase
import com.example.noteslist.domain.useCase.ShouldShowInitialShimmerUseCase
import com.example.noteslist.domain.useCase.ToggleNoteViewedUseCase
import com.example.noteslist.presentation.notes.toNoteListItems
import com.example.noteslist.presentation.state.NotesListUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
class NotesListViewModel @Inject constructor(
    getNotesUseCase: GetNotesUseCase,
    observeStackSettingsUseCase: ObserveStackSettingsUseCase,
    private val toggleNoteViewedUseCase: ToggleNoteViewedUseCase,
    private val shouldShowInitialShimmerUseCase: ShouldShowInitialShimmerUseCase,
    private val markInitialShimmerShownUseCase: MarkInitialShimmerShownUseCase,
    @DefaultDispatcher private val defaultCoroutineDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private companion object {
        const val MIN_SHIMMER_SHOW_TIME_MS = 500L
    }
    /** Заметки из репозитория */
    private val notesFlow : StateFlow<List<Note>> = getNotesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

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

    private val isInitialShimmerVisible = MutableStateFlow(false)

    /** состояние экрана в данный момент
     * получается из полученных из репозитория заметок, ID раскрытых стеков заметок и конкретного поискового запроса */
    val uiState : StateFlow<NotesListUiState> = combine(
        notesFlow,
        expandedStackIds,
        searchQuery,
        observeStackSettingsUseCase(),
        isInitialShimmerVisible,
    ) { notes, expandedStackIds, query, stackSettings, isShimmerVisible ->
        val filteredNotes = filterNotesByTitle(
            notes = notes,
            query = query,
        )

        NotesListUiState(
            items = filteredNotes.toNoteListItems(),
            expandedStackIds = expandedStackIds,
            searchQuery = query,
            stackSettings = stackSettings,
            isInitialShimmerVisible = isShimmerVisible,
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

    init {
        launchInitialShimmerIfNeeded()
    }

    private fun launchInitialShimmerIfNeeded() {
        viewModelScope.launch {
            if (!shouldShowInitialShimmerUseCase()) return@launch

            val startTime = SystemClock.elapsedRealtime()

            isInitialShimmerVisible.value = true

            /* ждем когда из Room придет непустой список заметок при первом запуске */
            notesFlow.first { notes ->
                notes.isNotEmpty()
            }

            val elapsedTime = SystemClock.elapsedRealtime() - startTime
            val remainingTime = MIN_SHIMMER_SHOW_TIME_MS - elapsedTime

            if (remainingTime > 0) {
                delay(remainingTime)
            }

            isInitialShimmerVisible.value = false
            markInitialShimmerShownUseCase()
        }
    }

    fun onSearchQueryChanged(newQuery : String) {
        rawSearchQuery.value = newQuery
    }

    /** обработка длинного нажатия */
    fun onNoteLongClick(noteId : Long) {
        if (togglingViewedJob?.isActive == true) return

        togglingViewedJob = toggleNoteViewedUseCase(noteId)
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