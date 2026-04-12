package com.example.noteslist.presentation.editorhost

sealed interface EditorDestination {
    data object Closed : EditorDestination
    data object Create : EditorDestination
    data class Edit(val noteUiId : String) : EditorDestination
}