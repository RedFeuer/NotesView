package com.example.noteslist.presentation.notes.adapters.delegates

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.presentation.notes.NoteListItem

interface AdapterDelegate {
    /* Определяет, подходит ли делегат для текущего элемента списка.
    Если подходит, то делегат будет использоваться для отображения этого элемента. */
    fun isForViewType(item: NoteListItem): Boolean
    /* Создаем ViewHolder */
    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    /* Привязываем данные к ViewHolder */
    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: NoteListItem)
    fun onBindViewHolder(
        holder : RecyclerView.ViewHolder,
        item : NoteListItem,
        payloads : List<Any>,
    ) {
        onBindViewHolder(holder, item)
    }
}