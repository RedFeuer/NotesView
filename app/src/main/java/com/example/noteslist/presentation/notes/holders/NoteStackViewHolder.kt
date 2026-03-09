package com.example.noteslist.presentation.notes.holders

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.presentation.view.NoteStackView

class NoteStackViewHolder(
    private val noteStackView: NoteStackView,
) : RecyclerView.ViewHolder(noteStackView) {

    fun bind(notes: List<Note>) {
        noteStackView.submitNotes(notes)
    }

    companion object {
        /* фабричный метод для создания экземпляра ViewHolder, который будет использоваться в адаптере */
        fun create (parent: android.view.ViewGroup) : NoteStackViewHolder {
            val noteStackView = NoteStackView(parent.context).apply {
                layoutParams = RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            return NoteStackViewHolder(noteStackView)
        }
    }
}