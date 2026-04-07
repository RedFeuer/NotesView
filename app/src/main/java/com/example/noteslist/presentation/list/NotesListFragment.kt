package com.example.noteslist.presentation.list

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.R
import com.example.noteslist.data.repositoryImpl.NotesRepositoryImpl
//import com.example.noteslist.presentation.editor.NoteEditorActivity
import androidx.navigation.fragment.findNavController
import com.example.noteslist.presentation.notes.adapters.NotesListAdapter
import com.example.noteslist.presentation.notes.toNoteListItems
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NotesListFragment : Fragment(R.layout.fragment_notes_list) {
    /** Singleton репозитория для актуальности заметок */
    private val repository = NotesRepositoryImpl.instance
    /** множество раскрытых стеков заметок, где идентификатор - id стека*/
    private var expandedStackIds = mutableSetOf<String>()

    private lateinit var notesAdapter : NotesListAdapter
    private lateinit var fabAddNote : FloatingActionButton
    private lateinit var recyclerViewNotes : RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerViewNotes = view.findViewById<RecyclerView>(R.id.recycler_view_notes)
        fabAddNote = view.findViewById<FloatingActionButton>(R.id.fab_add_note)

        notesAdapter = NotesListAdapter(
            /* обработка клика - редактирование заметки */
            onNoteClick = { note ->
                val direction =
                    NotesListFragmentDirections.actionNotesListFragmentToNoteEditorFragment(
                        note = note,
                        isEditMode = true,
                    )
                findNavController().navigate(direction)
            },
            onNoteLongClick = { note ->
                repository.toggleViewed(note.uiId)
                renderNotes() // перерисовываем список, чтобы отобразилась прочитанной нужную заметку
            },
            isStackExpanded = { stackId ->
                expandedStackIds.contains(stackId)
            },
            onStackExpandedChange = { stackId, isExpanded ->
                if (isExpanded) {
                    expandedStackIds.add(stackId) // добавляем в множество заметок
                } else {
                    expandedStackIds.remove(stackId) // убираем из множества заметок
                }
            },
        )

        recyclerViewNotes.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewNotes.adapter = notesAdapter

        /* обработка нажатия по Floating Action Button добавления новой заметки */
        fabAddNote.setOnClickListener {
            val direction =
                NotesListFragmentDirections.actionNotesListFragmentToNoteEditorFragment(
                    note = null,
                    isEditMode = false,
                )
            findNavController().navigate(direction)
        }

        recyclerViewNotes.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            /** при любом вертикальном скроле скрываем FAB кнопку */
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) {
                    fabAddNote.hide()
                }
            }

            /** при любой остановке скрола FAB кнопка появляется */
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    /* post чтобы анимация корректнее отображалась при быстрых скроллах туда-сюда */
                    fabAddNote.post { fabAddNote.show() }
                }
            }
        })

        renderNotes()
    }

    /** при возврате на экран заново рендерим, чтобы отображать актуальный UI */
    override fun onResume() {
        super.onResume()
        renderNotes()
    }

    /** прикрепление списка заметок к экрану приложения */
    private fun renderNotes() {
        notesAdapter.submitList(repository.getNotes().toNoteListItems())
    }
}