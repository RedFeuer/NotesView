package com.example.noteslist.presentation.notes.adapters.delegates

import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.presentation.notes.NoteListItem
import com.example.noteslist.presentation.notes.holders.ImportantNoteViewHolder

class ImportantNoteAdapterDelegate : AdapterDelegate {
    override fun isForViewType(item: NoteListItem): Boolean {
        return item is NoteListItem.ImportantNoteItem
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return ImportantNoteViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: NoteListItem) {
        val importantNoteItem = item as NoteListItem.ImportantNoteItem
        (holder as ImportantNoteViewHolder).bind(importantNoteItem.note)
    }
}