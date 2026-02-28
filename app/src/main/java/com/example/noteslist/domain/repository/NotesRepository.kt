package com.example.noteslist.domain.repository

import com.example.noteslist.domain.domainModel.Note

interface NotesRepository {
    fun getNotes() : List<Note>
}