package com.example.noteslist.domain.useCase

import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.domain.repository.NotesRepository
import javax.inject.Inject

class GetNoteByIdUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
) {
    suspend operator fun invoke(noteId: Long) : Note? {
        return notesRepository.getNoteById(noteId)
    }
}