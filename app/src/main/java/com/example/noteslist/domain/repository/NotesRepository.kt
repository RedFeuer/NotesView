package com.example.noteslist.domain.repository

import com.example.noteslist.domain.domainModel.Note

interface NotesRepository {
    fun getNotes() : List<Note>
    fun getNoteById(uiId : String): Note?
    fun addNote(note : Note)
    fun updateNote(note : Note)
    fun toggleViewed(uiId : String) // помечаем прочитанным посредством длинного тапа
}