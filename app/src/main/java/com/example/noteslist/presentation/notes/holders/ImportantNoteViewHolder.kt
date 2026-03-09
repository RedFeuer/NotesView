package com.example.noteslist.presentation.notes.holders

import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.presentation.view.NoteMapper
import com.example.noteslist.presentation.view.NoteView

class ImportantNoteViewHolder(
    private val noteView: NoteView
) : RecyclerView.ViewHolder(noteView) {
    private val noteMapper = NoteMapper()

    fun bind(note: Note) {
        val noteUi = noteMapper.mapDomainModelToUi(note)
        noteView.bind(noteUi)
    }
}