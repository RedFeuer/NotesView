package com.example.noteslist.presentation.notes.adapters.delegates

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.presentation.notes.NoteListItem
import com.example.noteslist.presentation.notes.holders.NoteStackViewHolder

class NoteStackAdapterDelegate(
    private val onNoteClick : (Note) -> Unit,
    private val onNoteLongClick : (Note) -> Unit,
    ): AdapterDelegate {
    override fun isForViewType(item: NoteListItem): Boolean {
        return item is NoteListItem.NoteStackItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return NoteStackViewHolder.create(
            parent = parent,
            onNoteClick = onNoteClick,
            onNoteLongClick = onNoteLongClick,
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: NoteListItem) {
        val noteStackItem = item as NoteListItem.NoteStackItem
        (holder as NoteStackViewHolder).bind(noteStackItem.notes)
    }
}