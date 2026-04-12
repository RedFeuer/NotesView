package com.example.noteslist.presentation.viewModel

import androidx.lifecycle.ViewModel
import com.example.noteslist.data.repositoryImpl.NotesRepositoryImpl
import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.presentation.state.NoteEditorUiState
import com.example.noteslist.presentation.view.NoteMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

class NoteEditorViewModel(
    /** Singleton репозитория для актуальности заметок */
//    private val repository : NotesRepositoryImpl
) : ViewModel() {
    private val repository = NotesRepositoryImpl.instance
    private val noteMapper = NoteMapper()

    private val _uiState = MutableStateFlow(NoteEditorUiState())
    val uiState : StateFlow<NoteEditorUiState> = _uiState.asStateFlow()

    /** исходная заметка для режима редактирования */
    private var sourceNote : Note? = null
    private var isInitialized : Boolean = false

    fun init(note : Note?, isEditMode : Boolean) {
        if (isInitialized) return
        isInitialized = true

        sourceNote = note

        val title = note?.title.orEmpty()
        val description = note?.description.orEmpty()
        val isImportant = note?.isImportant ?: false
        val isViewed = note?.isViewed ?: false
        val createdAtText = note?.createdAtMillis?.let {
            noteMapper.createdAtFormatter.format(Date(it))
        }.orEmpty()

        _uiState.value = NoteEditorUiState(
            title = title,
            description = description,
            isImportant = isImportant,
            isViewed = isViewed,
            createdAtText = createdAtText,
            isEditMode = isEditMode,
            initialTitle = title,
            initialDescription = description,
            initialIsImportant = isImportant,
            initialIsViewed = isViewed,
        )
    }

    fun onTitleChanged(newTitle : String) {
        _uiState.value = _uiState.value.copy(
            title = newTitle,
            showEmptyTitleError = if (newTitle.isNotBlank()) {
                false
            } else {
                _uiState.value.showEmptyTitleError
            }
        )
    }

    fun onDescriptionChanged(newDescription : String) {
        _uiState.value = _uiState.value.copy(
            description = newDescription
        )
    }

    fun onIsImportantChanged(newIsImportant : Boolean) {
        _uiState.value = _uiState.value.copy(
            isImportant = newIsImportant
        )
    }

    fun onIsViewedChanged(newIsViewed : Boolean) {
        _uiState.value = _uiState.value.copy(
            isViewed = newIsViewed
        )
    }

    fun saveNote() : Boolean {
        val current = _uiState.value

        /* предупреждение, что надо дать заметке название */
        if (current.title.isBlank()) {
            _uiState.value = current.copy(showEmptyTitleError = true)
            return false
        }

        if (current.isEditMode) {
            val oldNote = sourceNote ?: return false

            repository.updateNote(
                oldNote.copy(
                    title = current.title.trim(),
                    description = current.description.takeIf { it.isNotBlank() },
                    isImportant = current.isImportant,
                    isViewed = current.isViewed,
                )
            )
        } else {
            repository.addNote(
                Note(
                    title = current.title.trim(),
                    description = current.description.takeIf { it.isNotBlank() },
                    isImportant = current.isImportant,
                )
            )
        }

        return true
    }
}