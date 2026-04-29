package com.example.noteslist.data.demo

import com.example.noteslist.domain.demo.DemoNotesSource
import com.example.noteslist.domain.domainModel.Note
import javax.inject.Inject

class DemoNotesSourceImpl @Inject constructor() : DemoNotesSource {
    override fun createDemoNotes(): List<Note> {
        return listOf(
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
                title = "Безопасность операционных систем",
                description = "Отмечать неточности в книге преподавателя и писать ему, чтобы он их исправлял"
            ),
            Note(
                title = "Сервис",
                description = "Оплатить подписку",
            ),
            /* прошлые сутки, чтобы проверить еще один стек */
            Note(
                title = "НИР",
                description = "Согласовать задачи на семестр с научным руководителем",
                createdAtMillis = System.currentTimeMillis() - (24*60*60*1000)
            ),
            Note(
                title = "Резюме",
//                isImportant = true,
                description = "Добавить новый проект с BLE в резюме",
                createdAtMillis = System.currentTimeMillis() - (24*60*60*1000)
            )
        )
    }
}