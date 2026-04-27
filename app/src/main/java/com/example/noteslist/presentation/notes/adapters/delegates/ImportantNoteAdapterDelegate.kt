package com.example.noteslist.presentation.notes.adapters.delegates

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.presentation.notes.NoteListItem
import com.example.noteslist.presentation.notes.holders.ImportantNoteViewHolder

class ImportantNoteAdapterDelegate(
    private val onNoteClick : (Note) -> Unit,
    private val onNoteLongClick : (Note) -> Unit,
) : AdapterDelegate {
    override fun isForViewType(item: NoteListItem): Boolean {
        return item is NoteListItem.ImportantNoteItem
    }

    /** создаем ViewHolder с колбеками на события клика и долгого клика */
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ImportantNoteViewHolder.create(
            parent = parent,
            onNoteClick = onNoteClick,
            onNoteLongClick = onNoteLongClick,
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: NoteListItem) {
        val importantNoteItem = item as NoteListItem.ImportantNoteItem
        (holder as ImportantNoteViewHolder).bind(importantNoteItem.note)
    }
}