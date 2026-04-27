package com.example.noteslist.data.repositoryImpl

import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.domain.repository.NotesRepository

class NotesRepositoryImpl : NotesRepository {
    /* TODO: потом сделать Flow<Note>, чтобы подписаться в ViewModel и реактивно отображать на экране */
    override fun getNotes() : List<Note> {
        return listOf(
            Note(
                title = "Т-Академия",
                isImportant = true,
                description = "Выполнить задание 1",
            ),
            Note(
                title = "Резюме",
                isImportant = true,
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
        )
    }
}