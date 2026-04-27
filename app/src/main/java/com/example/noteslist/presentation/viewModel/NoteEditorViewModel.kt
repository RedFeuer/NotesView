package com.example.noteslist.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.domain.useCase.CreateNoteUseCase
import com.example.noteslist.domain.useCase.GetNoteByIdUseCase
import com.example.noteslist.domain.useCase.UpdateNoteUseCase
import com.example.noteslist.presentation.state.NoteEditorUiState
import com.example.noteslist.presentation.view.NoteMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

class NoteEditorViewModel @Inject constructor(
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val createNoteUseCase: CreateNoteUseCase,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val noteMapper : NoteMapper,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NoteEditorUiState())
    val uiState : StateFlow<NoteEditorUiState> = _uiState.asStateFlow()

    /** исходная заметка для режима редактирования */
    private var sourceNote : Note? = null

    fun startCreate() {
        sourceNote = null
        _uiState.value = NoteEditorUiState(
            isEditMode = false,
        )
    }

    fun startEdit(noteUiId : String) {
        viewModelScope.launch {
            val note = getNoteByIdUseCase(noteUiId) ?: return@launch

            sourceNote = note

            val title = note.title ?: ""
            val description = note.description.orEmpty()
            val isImportant = note.isImportant
            val isViewed = note.isViewed
            val createdAtText = noteMapper.createdAtFormatter.format(Date(note.createdAtMillis))

            _uiState.value = NoteEditorUiState(
                title = title,
                description = description,
                isImportant = isImportant,
                isViewed = isViewed,
                createdAtText = createdAtText,
                isEditMode = true,
                initialTitle = title,
                initialDescription = description,
                initialIsImportant = isImportant,
                initialIsViewed = isViewed,
            )
        }
    }

    fun reset() {
        sourceNote = null
        _uiState.value = NoteEditorUiState()
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
        /* предупреждение, что надо дать заметке название */
        if (!validateTitleForSave()) {
            return false
        }

        val current = _uiState.value
        return if (current.isEditMode) {
            editNote(current)
        } else {
            createNewNote(current)
        }
    }

    private fun validateTitleForSave() : Boolean {
        val current = _uiState.value

        return if (current.title.isBlank()) {
            _uiState.value = current.copy(showEmptyTitleError = true)
            false
        } else {
            true
        }
    }

    private fun editNote(current: NoteEditorUiState) : Boolean {
        val oldNote = sourceNote ?: return false

        updateNoteUseCase(
            oldNote.copy(
                title = current.title.trim(),
                description = current.description.takeIf { it.isNotBlank() },
                isImportant = current.isImportant,
                isViewed = current.isViewed,
            )
        )

        return true
    }

    private fun createNewNote(current: NoteEditorUiState) : Boolean {
        createNoteUseCase(
            Note(
                title = current.title.trim(),
                description = current.description.takeIf { it.isNotBlank() },
                isImportant = current.isImportant,
            )
        )

        return true
    }
}