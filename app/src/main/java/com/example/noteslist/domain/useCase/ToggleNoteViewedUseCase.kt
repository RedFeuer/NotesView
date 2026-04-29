package com.example.noteslist.domain.useCase

import com.example.noteslist.di.coroutine.qualifier.ApplicationCoroutineScope
import com.example.noteslist.domain.repository.NotesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class ToggleNoteViewedUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
    @ApplicationCoroutineScope private val applicationCoroutineScope: CoroutineScope
) {
    operator fun invoke(noteId : Long) : Job {
        return applicationCoroutineScope.launch {
            notesRepository.toggleViewed(noteId)
        }
    }
}