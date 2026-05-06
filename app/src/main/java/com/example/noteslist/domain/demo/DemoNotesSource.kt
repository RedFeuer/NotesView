package com.example.noteslist.domain.demo

import com.example.noteslist.domain.domainModel.Note

interface DemoNotesSource {
    fun createDemoNotes() : List<Note>
}