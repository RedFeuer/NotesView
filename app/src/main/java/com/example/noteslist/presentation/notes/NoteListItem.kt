package com.example.noteslist.presentation.notes

import com.example.noteslist.domain.domainModel.Note
import java.util.Locale

sealed interface NoteListItem {
    /* идентификатор */
    val id: String
    /* заголовок для группы заметок с одинаковой датой создания */
    data class DateHeader(
        val date: NoteDate,
    ) : NoteListItem {
        override val id: String = "header_${date.toStableString()}"
    }

    /* важная заметка */
    data class ImportantNoteItem(
        val date: NoteDate,
        val note: Note,
    ) : NoteListItem {
        override val id: String = "important_${note.id}"
    }
    /* стек обычных заметок с одинаковой датой создания */
    data class NoteStackItem(
        val date: NoteDate,
        val notes: List<Note>,
    ) : NoteListItem {
        override val id: String = "stack_${date.toStableString()}"
    }
}
data class NoteDate(
    val year: Int,
    val month: Int,
    val day: Int,
) : Comparable<NoteDate> {
    override fun compareTo(other: NoteDate): Int {
        return when {
            year != other.year -> year.compareTo(other.year)
            month != other.month -> month.compareTo(other.month)
            else -> day.compareTo(other.day)
        }
    }

    fun toDisplayString(): String {
        return String.format(Locale.ROOT, "%02d.%02d.%04d", day, month, year)
    }

    fun toStableString(): String {
        return String.format(Locale.ROOT, "%04d-%02d-%02d", year, month, day)
    }
}