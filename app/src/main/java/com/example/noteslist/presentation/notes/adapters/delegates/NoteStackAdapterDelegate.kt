package com.example.noteslist.presentation.notes.adapters.delegates

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.presentation.notes.NoteListItem
import com.example.noteslist.presentation.notes.holders.NoteStackViewHolder

class NoteStackAdapterDelegate : AdapterDelegate {
    override fun isForViewType(item: NoteListItem): Boolean {
        return item is NoteListItem.NoteStackItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return NoteStackViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: NoteListItem) {
        val noteStackItem = item as NoteListItem.NoteStackItem
        (holder as NoteStackViewHolder).bind(noteStackItem.notes)
    }
}