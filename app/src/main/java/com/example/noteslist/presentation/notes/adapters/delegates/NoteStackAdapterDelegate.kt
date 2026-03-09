package com.example.noteslist.presentation.notes.adapters.delegates

import com.example.noteslist.presentation.notes.NoteListItem
import com.example.noteslist.presentation.notes.holders.NoteStackViewHolder

class NoteStackAdapterDelegate : AdapterDelegate {
    override fun isForViewType(item: NoteListItem): Boolean {
        return item is NoteListItem.NoteStackItem
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return NoteStackViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, item: NoteListItem) {
        val noteStackItem = item as NoteListItem.NoteStackItem
        (holder as NoteStackViewHolder).bind(noteStackItem.notes)
    }
}