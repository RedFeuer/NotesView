package com.example.noteslist.presentation.viewModel

import androidx.lifecycle.ViewModel
import com.example.noteslist.presentation.editorhost.EditorDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/** ViewModel обработки навигации между экраном заметок и экраном редактирования */
class EditorHostViewModel @Inject constructor() : ViewModel() {
    /** состояние навигации экранов */
    private val _destination = MutableStateFlow<EditorDestination>(EditorDestination.Closed)
    val destination : StateFlow<EditorDestination> = _destination.asStateFlow()

    fun openCreate() {
        _destination.value = EditorDestination.Create
    }

    fun openEdit(noteId : Long) {
        _destination.value = EditorDestination.Edit(noteId)
    }

    fun close() {
        _destination.value = EditorDestination.Closed
    }
}