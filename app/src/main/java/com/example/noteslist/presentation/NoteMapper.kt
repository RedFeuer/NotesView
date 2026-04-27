package com.example.noteslist.presentation

import com.example.noteslist.domain.domainModel.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteMapper {
    val createdAtFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    fun mapDomainModelToUi(note: Note): NoteUi {
        return NoteUi(
            title = note.title ?: "Название заметки",
            isImportant = note.isImportant,
            isViewed = note.isViewed,
            description = note.description ?: "Текст заметки",
            createdAt = createdAtFormatter.format(Date(note.createdAtMillis))
        )
    }
}