package com.example.noteslist.domain.useCase

import com.example.noteslist.di.coroutine.qualifier.ApplicationCoroutineScope
import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.domain.repository.NotesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
    @ApplicationCoroutineScope private val applicationCoroutineScope: CoroutineScope,
) {
    operator fun invoke(note: Note) : Job {
        return applicationCoroutineScope.launch {
            notesRepository.updateNote(note)
        }
    }
}