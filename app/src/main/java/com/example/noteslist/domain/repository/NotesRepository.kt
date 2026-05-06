package com.example.noteslist.domain.repository

import com.example.noteslist.domain.domainModel.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getNotes() : Flow<List<Note>>
    suspend fun getNoteById(id : Long): Note?
    suspend fun addNote(note : Note)
    suspend fun addNotes(notes : List<Note>)
    suspend fun updateNote(note : Note)
    suspend fun toggleViewed(id : Long) // помечаем прочитанным посредством длинного тапа
    suspend fun isEmpty() : Boolean
}