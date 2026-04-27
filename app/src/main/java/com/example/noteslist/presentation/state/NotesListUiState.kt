package com.example.noteslist.presentation.state

import com.example.noteslist.presentation.notes.NoteListItem

data class NotesListUiState(
    val items : List<NoteListItem> = emptyList(),
    val expandedStackIds : Set<String> = emptySet(),
    val searchQuery : String = "",
)