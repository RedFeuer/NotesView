package com.example.noteslist.presentation.notes.adapters

import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.domain.settings.StackSettings
import com.example.noteslist.presentation.notes.adapters.delegates.DateHeaderAdapterDelegate
import com.example.noteslist.presentation.notes.adapters.delegates.ImportantNoteAdapterDelegate
import com.example.noteslist.presentation.notes.adapters.delegates.MultiTypeAdapter
import com.example.noteslist.presentation.notes.adapters.delegates.NoteStackAdapterDelegate
import com.example.noteslist.presentation.notes.util.NoteListItemDiffUtilCallback

class NotesListAdapter private constructor(
    onNoteClick: (Note) -> Unit,
    onNoteLongClick: (Note) -> Unit,
    isStackExpanded: (String) -> Boolean,
    onStackExpandedChange: (String, Boolean) -> Unit,
    private val stackSettingsHolder: StackSettingsHolder,
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
            stackSettingsProvider = {
                stackSettingsHolder.settings
            },
        ),
    ),
) {

    constructor(
        onNoteClick: (Note) -> Unit,
        onNoteLongClick: (Note) -> Unit,
        isStackExpanded: (String) -> Boolean,
        onStackExpandedChange: (String, Boolean) -> Unit,
    ) : this(
        onNoteClick = onNoteClick,
        onNoteLongClick = onNoteLongClick,
        isStackExpanded = isStackExpanded,
        onStackExpandedChange = onStackExpandedChange,
        stackSettingsHolder = StackSettingsHolder(),
    )

    fun submitStackSettings(newSettings: StackSettings) {
        if (stackSettingsHolder.settings == newSettings) return

        stackSettingsHolder.settings = newSettings

        notifyItemRangeChanged(0, itemCount)
    }
}

private class StackSettingsHolder(
    var settings: StackSettings = StackSettings(),
)