package com.example.noteslist.presentation.notes.holders

import android.view.ViewGroup
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

    companion object {
        /* фабричный метод для создания экземпляра ViewHolder, который будет использоваться в адаптере */
        fun create (parent: ViewGroup) : ImportantNoteViewHolder {
            val noteView = NoteView(parent.context)
            return ImportantNoteViewHolder(noteView)
        }
    }
}