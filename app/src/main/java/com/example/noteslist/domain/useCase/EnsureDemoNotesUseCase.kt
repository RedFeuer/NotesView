package com.example.noteslist.domain.useCase

import com.example.noteslist.di.coroutine.qualifier.ApplicationCoroutineScope
import com.example.noteslist.domain.demo.DemoNotesSource
import com.example.noteslist.domain.repository.NotesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class EnsureDemoNotesUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
    private val demoNotesSource: DemoNotesSource,
    @ApplicationCoroutineScope private val applicationCoroutineScope: CoroutineScope,
) {
    fun launch() : Job {
        return applicationCoroutineScope.launch {
            if (!notesRepository.isEmpty()) return@launch

            notesRepository.addNotes(demoNotesSource.createDemoNotes())
        }
    }
}