package com.example.noteslist.presentation.notes.holders

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.presentation.notes.NoteListItem
import com.example.noteslist.presentation.view.NoteStackView

class NoteStackViewHolder(
    private val noteStackView: NoteStackView,
) : RecyclerView.ViewHolder(noteStackView) {

    /* биндим элемент к ViewHolder */
    fun bind(
        item: NoteListItem.NoteStackItem,
        isExpanded : Boolean,
        onExpandedChange : (Boolean) -> Unit,
    ) {
        noteStackView.setOnExpandedChange(onExpandedChange)
        noteStackView.submitNotes(item.notes, isExpanded)
    }

    companion object {
        /* фабричный метод для создания экземпляра ViewHolder, который будет использоваться в адаптере */
        fun create (
            parent: ViewGroup,
            onNoteClick : (Note) -> Unit,
            onNoteLongClick : (Note) -> Unit,
        ) : NoteStackViewHolder {
            val noteStackView = NoteStackView(parent.context).apply {
                layoutParams = RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                setNoteActions(
                    onNoteClick = onNoteClick,
                    onNoteLongClick = onNoteLongClick,
                )
            }
            return NoteStackViewHolder(noteStackView)
        }
    }
}