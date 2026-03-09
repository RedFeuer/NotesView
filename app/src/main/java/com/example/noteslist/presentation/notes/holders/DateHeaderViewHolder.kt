package com.example.noteslist.presentation.notes.holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.R
import com.example.noteslist.presentation.notes.NoteListItem

class DateHeaderViewHolder(
    itemView: View,
) : RecyclerView.ViewHolder(itemView) {
    /* TODO: переделать на ViewBinding */
    private val tvDateHeader: TextView = itemView.findViewById(R.id.textViewDateHeader)

    fun bind(item: NoteListItem.DateHeader) {
        tvDateHeader.text = item.date.toDisplayString()
    }

    companion object {
        /* фабричный метод для создания экземпляра ViewHolder, который будет использоваться в адаптере */
        fun create (parent: ViewGroup) : DateHeaderViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_note_date_header, parent, false)
            return DateHeaderViewHolder(view)
        }
    }
}