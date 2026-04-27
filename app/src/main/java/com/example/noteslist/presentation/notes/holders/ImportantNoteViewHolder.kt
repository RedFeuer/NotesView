package com.example.noteslist.presentation.notes.holders

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.presentation.view.NoteMapper
import com.example.noteslist.presentation.view.NoteView

class ImportantNoteViewHolder(
    private val noteView: NoteView,
    private val noteMapper: NoteMapper = NoteMapper(),
    private val onNoteClick : (Note) -> Unit,
    private val onNoteLongClick : (Note) -> Unit,
) : RecyclerView.ViewHolder(noteView) {

    /* биндим элемент к ViewHolder */
    fun bind(note: Note) {
        val noteUi = noteMapper.mapDomainModelToUi(note)
        noteView.bind(noteUi)

        /* добавили ClickListener'ы для поддержки передачи событий длинного тапа и обынчого */
        noteView.setOnClickListener {
            onNoteClick(note)
        }

        noteView.setOnLongClickListener {
            onNoteLongClick(note)
            true
        }
    }

    companion object {
        /* фабричный метод для создания экземпляра ViewHolder, который будет использоваться в адаптере */
        fun create (
            parent: ViewGroup,
            onNoteClick : (Note) -> Unit,
            onNoteLongClick : (Note) -> Unit,
        ) : ImportantNoteViewHolder {
            val noteView = NoteView(parent.context).apply {
                layoutParams = RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            return ImportantNoteViewHolder(
                noteView = noteView,
                onNoteClick = onNoteClick,
                onNoteLongClick = onNoteLongClick,
            )
        }
    }
}