package com.example.noteslist.data.repositoryImpl

import com.example.noteslist.di.coroutine.qualifier.IoDispatcher
import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.domain.repository.NotesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher : CoroutineDispatcher,
) : NotesRepository {
    private val notesFlow = MutableStateFlow(
        listOf(
            Note(
                title = "Т-Академия",
                isImportant = true,
                description = "Выполнить задание 3",
            ),
            Note(
                title = "Работа",
                isImportant = true,
                description = "Пройти испытательный срок"
            ),
            Note(
                title = "Резюме",
//                isImportant = true,
                description = "Добавить новый проект с BLE в резюме"
            ),
            Note(
                title = "НИР",
                description = "Согласовать задачи на семестр с научным руководителем"
            ),
            Note(
                title = "Безопасность операционных систем",
                description = "Отмечать неточности в книге преподавателя и писать ему, чтобы он их исправлял"
            ),
            Note(
                title = "Сервис",
                description = "Оплатить подписку",
            ),
            /* прошлые сутки, чтобы проверить еще один стек */
            Note(
                title = "Пип гупип",
                description = "Гугииии",
                createdAtMillis = System.currentTimeMillis() - (24*60*60*1000)
            ),
            Note(
                title = "Пип гупипgbgbgb",
                description = "Гугииииgbgbgb",
                createdAtMillis = System.currentTimeMillis() - (24*60*60*1000)
            )
        )
    )

    override fun getNotes(): Flow<List<Note>> {
        return notesFlow
            .map { notes ->
                notes.sortedByDescending { it.createdAtMillis }
            }
            .distinctUntilChanged()
    }

    override suspend fun getNoteById(uiId: String): Note? {
        return notesFlow.value.find { note -> note.uiId == uiId }
    }

    override suspend fun addNote(note: Note) {
        notesFlow.update { oldNotes ->
            oldNotes + note
        }
    }

    override suspend fun updateNote(note: Note) {
        notesFlow.update { oldNotes ->
            oldNotes.map { oldNote ->
                if (oldNote.uiId == note.uiId) {
                    note
                } else {
                    oldNote
                }
            }
        }
    }

    override suspend fun toggleViewed(uiId: String) {
        notesFlow.update { oldNotes ->
            oldNotes.map { oldNote ->
                if (oldNote.uiId == uiId) {
                    oldNote.copy(isViewed = !oldNote.isViewed)
                } else {
                    oldNote
                }
            }
        }
    }
}