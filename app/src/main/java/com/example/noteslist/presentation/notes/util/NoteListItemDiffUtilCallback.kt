package com.example.noteslist.presentation.notes.util

import androidx.recyclerview.widget.DiffUtil
import com.example.noteslist.presentation.notes.NoteListItem

/* DiffUtil для оптимального обновления списка заметок при изменении данных */
class NoteListItemDiffUtilCallback : DiffUtil.ItemCallback<NoteListItem>() {
    override fun areItemsTheSame(
        oldItem: NoteListItem,
        newItem: NoteListItem
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: NoteListItem,
        newItem: NoteListItem
    ): Boolean {
        return oldItem == newItem
    }
}