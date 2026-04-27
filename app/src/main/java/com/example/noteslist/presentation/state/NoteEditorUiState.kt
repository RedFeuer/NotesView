package com.example.noteslist.presentation.state

data class NoteEditorUiState(
    val title : String = "",
    val description : String = "",
    val isImportant : Boolean = false,
    val isViewed : Boolean = false,
    val showEmptyTitleError : Boolean = false,
    val showTitleTooLongError : Boolean = false,
    val createdAtText : String = "",
    val isEditMode : Boolean = false,

    /** исходные значения для проверки изменний */
    val initialTitle : String = "",
    val initialDescription : String = "",
    val initialIsImportant : Boolean = false,
    val initialIsViewed : Boolean = false,
) {
    val isChanged : Boolean
        get() = (title != initialTitle ||
                description != initialDescription ||
                isImportant != initialIsImportant ||
                isViewed != initialIsViewed)
}