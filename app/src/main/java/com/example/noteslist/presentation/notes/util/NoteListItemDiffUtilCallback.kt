package com.example.noteslist.presentation.notes.util

import androidx.recyclerview.widget.DiffUtil
import com.example.noteslist.presentation.notes.NoteListItem

/* DiffUtil для оптимального обновления списка заметок при изменении данных */
class NoteListItemDiffUtilCallback : DiffUtil.ItemCallback<NoteListItem>() {
    /* для стека */
    override fun areItemsTheSame(
        oldItem: NoteListItem,
        newItem: NoteListItem
    ): Boolean {
        return oldItem.id == newItem.id
    }

    /* для стека */
    override fun areContentsTheSame(
        oldItem: NoteListItem,
        newItem: NoteListItem
    ): Boolean {
        return oldItem == newItem
    }

    /* для одной измененной заметки */
    override fun getChangePayload(
        oldItem: NoteListItem,
        newItem: NoteListItem
    ): Any? {
        if (oldItem is NoteListItem.NoteStackItem &&
            newItem is NoteListItem.NoteStackItem) {
            return getStackChangePayload(oldItem, newItem)
        }
        return null
    }

    private fun getStackChangePayload(
        oldItem: NoteListItem.NoteStackItem,
        newItem: NoteListItem.NoteStackItem,
    ) : NoteListItemPayload? {
        /* это уже не та заметка из стека */
        if (oldItem.id != newItem.id) return null

        /* если размер стека изменился */
        if (oldItem.notes.size != newItem.notes.size) return null

        /* проверяем, что состав заметок тот же и порядок тот же */
        val sameNoteOrder = oldItem.notes.zip(newItem.notes).all { (oldNote, newNote) ->
            oldNote.uiId == newNote.uiId
        }
        if (!sameNoteOrder) return null

        val changedPairs = oldItem.notes.zip(newItem.notes).filter { (oldNote, newNote) ->
            oldNote != newNote
        }

        /* payload только для случая, когда изменилась ровно одна заметка
        * если поменялось больше одной заметки - перерисовываем весь стек
        * если поменялась одна заметочка - перерисовываем ее*/
        if (changedPairs.size != 1) return null

        /* то, что нам нужно, чтобы менять только одну заметку при ее изменении */
        val (_, newChangedNote) = changedPairs.single()

        return NoteListItemPayload.StackNoteChanged(
            noteUiId = newChangedNote.uiId,
            newNote = newChangedNote
        )
    }
}