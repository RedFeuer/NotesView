package com.example.noteslist.presentation.notes.util

import com.example.noteslist.domain.domainModel.Note

sealed interface NoteListItemPayload {
    data class StackNoteChanged(
        /** id заметки, которая изменилась  */
        val noteUiId : String,
        /** новая версия заметки */
        val newNote : Note,
    ) : NoteListItemPayload
}