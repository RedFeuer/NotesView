package com.example.noteslist.presentation.notes.holders

import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.presentation.view.NoteStackView

class NoteStackViewHolder(
    private val noteStackView: NoteStackView,
) : RecyclerView.ViewHolder(noteStackView) {

    fun bind(notes: List<Note>) {
        noteStackView.submitNotes(notes)
    }
}