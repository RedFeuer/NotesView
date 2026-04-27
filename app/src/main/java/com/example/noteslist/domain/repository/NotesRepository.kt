package com.example.noteslist.domain.repository

import com.example.noteslist.domain.domainModel.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getNotes() : Flow<List<Note>>
    suspend fun getNoteById(uiId : String): Note?
    suspend fun addNote(note : Note)
    suspend fun updateNote(note : Note)
    suspend fun toggleViewed(uiId : String) // помечаем прочитанным посредством длинного тапа
}