package com.example.noteslist.data.demo

import com.example.noteslist.domain.demo.DemoNotesSource
import com.example.noteslist.domain.domainModel.Note
import javax.inject.Inject

class DemoNotesSourceImpl @Inject constructor() : DemoNotesSource {
    override fun createDemoNotes(): List<Note> {
        return listOf(
            Note(
                title = "Важная заметка 1",
                isImportant = true,
                description = "Текст важной заметки",
            ),
            Note(
                title = "Важная заметка 2",
                isImportant = true,
                description = "Текст важной заметки"
            ),
            Note(
                title = "Заметка 1",
                description = "Текст заметки"
            ),
            Note(
                title = "Заметка 2",
                description = "Текст заметки",
            ),
            Note(
                title = "Заметка 3",
                description = "Текст заметки",
            ),
            Note(
                title = "Заметка 4",
                description = "Текст заметки",
            ),
            Note(
                title = "Заметка 5",
                description = "Текст заметки",
            ),
            /* прошлые сутки, чтобы проверить еще один стек */
            Note(
                title = "Вчерашняя заметка 1",
                description = "Согласовать задачи на семестр с научным руководителем",
                createdAtMillis = System.currentTimeMillis() - (24*60*60*1000)
            ),
            Note(
                title = "Вчерашняя заметка 2",
                description = "Согласовать задачи на семестр с научным руководителем",
                createdAtMillis = System.currentTimeMillis() - (24*60*60*1000)
            ),
            Note(
                title = "Вчерашняя заметка 3",
                description = "Согласовать задачи на семестр с научным руководителем",
                createdAtMillis = System.currentTimeMillis() - (24*60*60*1000)
            ),
            Note(
                title = "Вчерашняя заметка 4",
                description = "Согласовать задачи на семестр с научным руководителем",
                createdAtMillis = System.currentTimeMillis() - (24*60*60*1000)
            )
        )
    }
}