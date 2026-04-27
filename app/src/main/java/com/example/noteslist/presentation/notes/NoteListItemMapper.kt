package com.example.noteslist.presentation.notes

import android.icu.util.Calendar
import com.example.noteslist.domain.domainModel.Note

/* функция-расширения для маппинга List<Note> -> List<NoteListItem> */
fun List<Note>.toNoteListItems(): List<NoteListItem> {
    if (isEmpty()) return emptyList()

    /* сортируем заметки по дате создания, чтобы при группировке они были в нужном порядке */
    val notesSortedByCreatedAt = this.sortedByDescending { it.createdAtMillis }

    /* группируем заметки по дате создания, используя вспомогательную функцию toNoteDate() */
    val notesGroupedByDate = notesSortedByCreatedAt.groupBy { note ->
        note.createdAtMillis.toNoteDate()
    }

    return buildList {
        notesGroupedByDate.keys
            .sortedDescending()
            .forEach { date ->
                /* добавляем заголовок для группы заметок с одинаковой датой создания */
                add(NoteListItem.DateHeader(date))

                /* все заметки с одинаковой датой создания */
                val notesOfDay = notesGroupedByDate.getValue(date)

                /* разделяем заметки по факту важности */
                val (importantNotes, regularNotes) = notesOfDay.partition { it.isImportant }

                /* добавляем важные заметки по отдельности, чтобы они отображались в виде отдельных элементов списка */
                importantNotes.forEach { note ->
                    add(NoteListItem.ImportantNoteItem(
                        date = date,
                        note = note
                        )
                    )
                }

                /* добавляем обычные заметки в виде стека, чтобы они отображались в виде одного элемента списка с количеством заметок внутри */
                if (regularNotes.isNotEmpty()) {
                    add(NoteListItem.NoteStackItem(
                        date = date,
                        notes = regularNotes
                        )
                    )
                }
            }
    }
}

/* вспомогательная функция, которая из Long создает NoteDate */
private fun Long.toNoteDate(): NoteDate {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = this@toNoteDate
    }

    return NoteDate(
        year = calendar.get(Calendar.YEAR),
        month = calendar.get(Calendar.MONTH) + 1, // так как Calendar.MONTH возвращает 0-11
        day = calendar.get(Calendar.DAY_OF_MONTH)
    )
}