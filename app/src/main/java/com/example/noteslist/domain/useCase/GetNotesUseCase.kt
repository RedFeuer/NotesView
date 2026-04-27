package com.example.noteslist.domain.useCase

import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotesUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
) {
    operator fun invoke() : Flow<List<Note>> {
        return notesRepository.getNotes()
    }
}