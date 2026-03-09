package com.example.noteslist.presentation.notes.adapters

import com.example.noteslist.presentation.notes.adapters.delegates.DateHeaderAdapterDelegate
import com.example.noteslist.presentation.notes.adapters.delegates.ImportantNoteAdapterDelegate
import com.example.noteslist.presentation.notes.adapters.delegates.MultiTypeAdapter
import com.example.noteslist.presentation.notes.adapters.delegates.NoteStackAdapterDelegate
import com.example.noteslist.presentation.notes.util.NoteListItemDiffUtilCallback

class NotesListAdapter : MultiTypeAdapter(
    diffUtilCallback = NoteListItemDiffUtilCallback(),
    delegates = listOf(
        DateHeaderAdapterDelegate(),
        ImportantNoteAdapterDelegate(),
        NoteStackAdapterDelegate(),
    ),
)