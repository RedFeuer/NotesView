package com.example.noteslist.presentation.notes.adapters

import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.presentation.notes.adapters.delegates.DateHeaderAdapterDelegate
import com.example.noteslist.presentation.notes.adapters.delegates.ImportantNoteAdapterDelegate
import com.example.noteslist.presentation.notes.adapters.delegates.MultiTypeAdapter
import com.example.noteslist.presentation.notes.adapters.delegates.NoteStackAdapterDelegate
import com.example.noteslist.presentation.notes.util.NoteListItemDiffUtilCallback

class NotesListAdapter(
    private val onNoteClick: (Note) -> Unit,
    private val onNoteLongClick: (Note) -> Unit,
    private val isStackExpanded: (String) -> Boolean,
    private val onStackExpandedChange: (String, Boolean) -> Unit,
) : MultiTypeAdapter(
    diffUtilCallback = NoteListItemDiffUtilCallback(),
    delegates = listOf(
        DateHeaderAdapterDelegate(),
        ImportantNoteAdapterDelegate(onNoteClick, onNoteLongClick),
        NoteStackAdapterDelegate(
            onNoteClick = onNoteClick,
            onNoteLongClick = onNoteLongClick,
            isStackExpanded = isStackExpanded,
            onStackExpandedChange = onStackExpandedChange,
        ),
    ),
)