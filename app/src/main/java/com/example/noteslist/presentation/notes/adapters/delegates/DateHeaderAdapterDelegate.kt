package com.example.noteslist.presentation.notes.adapters.delegates

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.presentation.notes.NoteListItem
import com.example.noteslist.presentation.notes.holders.DateHeaderViewHolder

class DateHeaderAdapterDelegate : AdapterDelegate {
    override fun isForViewType(item: NoteListItem): Boolean {
        return item is NoteListItem.DateHeader
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return DateHeaderViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: NoteListItem) {
        val dateHeaderItem = item as NoteListItem.DateHeader
        (holder as DateHeaderViewHolder).bind(dateHeaderItem)
    }
}