package com.example.noteslist.data.repositoryImpl

import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.domain.repository.NotesRepository

class NotesRepositoryImpl : NotesRepository {
    /* TODO: потом сделать Flow<Note>, чтобы подписаться в ViewModel и реактивно отображать на экране */
    private val notes = mutableListOf<Note>(
        //            Note(
//                title = "Т-Академия",
//                isImportant = true,
//                description = "Выполнить задание 1",
//            ),
        Note(
            title = "Резюме",
//                isImportant = true,
            description = "Добавить новый проект с BLE в резюме"
        ),
        Note(
            title = "НИР",
            description = "Согласовать задачи на семестр с научным руководителем"
        ),
        Note(
            title = "Безопасность операционных систем",
            description = "Отмечать неточности в книге преподавателя и писать ему, чтобы он их исправлял"
        ),
        Note(
            title = "Сервис",
            description = "Оплатить подписку",
        ),
    )

    override fun getNotes(): List<Note> {
        return notes.sortedByDescending { it.createdAtMillis }
    }

    override fun getNoteById(uiId: String): Note? {
        return notes.find { it.uiId == uiId }
    }

    override fun addNote(note: Note) {
        notes.add(note)
    }

    override fun updateNote(note: Note) {
        val index = notes.indexOfFirst { it.uiId == note.uiId }
        if (index != -1) {
            notes[index] = note
        }
    }

    override fun toggleViewed(uiId: String) {
        val index = notes.indexOfFirst { it.uiId == uiId }
        if (index != -1) {
            val oldNote = notes[index]
            notes[index] = oldNote.copy(isViewed = !oldNote.isViewed)
        }
    }
}