package com.example.noteslist.presentation.notes.holders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.R
import com.example.noteslist.presentation.notes.NoteListItem

class DataHeaderViewHolder(
    itemView: View,
) : RecyclerView.ViewHolder(itemView) {
    /* TODO: переделать на ViewBinding */
    private val tvDateHeader: TextView = itemView.findViewById(R.id.textViewDateHeader)

    fun bind(item: NoteListItem.DataHeader) {
        tvDateHeader.text = item.date.toDisplayString()
    }
}