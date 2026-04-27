package com.example.noteslist.presentation.list

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.R
import com.example.noteslist.domain.repository.NotesRepository
import com.example.noteslist.presentation.notes.adapters.NotesListAdapter
import com.example.noteslist.presentation.notes.toNoteListItems
import com.example.noteslist.presentation.view.MainActivity
import com.example.noteslist.presentation.viewModel.EditorHostViewModel
import com.example.noteslist.presentation.viewModel.NoteEditorViewModel
import com.example.noteslist.presentation.viewModel.NotesListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotesListFragment : Fragment(R.layout.fragment_notes_list) {
    /** Фабрика ViewModel'ей */
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    /** ViewModel обработки навигации экранов (список - редактирование) */
    private val editorHostViewModel : EditorHostViewModel by activityViewModels {
        viewModelFactory
    }
    /** ViewModel состояния списка заметок */
    private val notesListViewModel : NotesListViewModel by activityViewModels {
        viewModelFactory
    }

    private val noteEditorViewModel : NoteEditorViewModel by activityViewModels {
        viewModelFactory
    }

    private lateinit var notesAdapter : NotesListAdapter
    private lateinit var fabAddNote : FloatingActionButton
    private lateinit var recyclerViewNotes : RecyclerView

    /** Прицепляем фрагмент к MainActivity */
    override fun onAttach(context: Context) {
        (context as MainActivity)
            .activityComponent
            .notesListFragmentComponentFactory()
            .create()
            .inject(this)

        super.onAttach(context)
    }

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
                noteEditorViewModel.startEdit(note.uiId)
                editorHostViewModel.openEdit(note.uiId)
            },
            onNoteLongClick = { note ->
                notesListViewModel.onNoteLongClick(note.uiId)
            },
            isStackExpanded = { stackId ->
                notesListViewModel.uiState.value.expandedStackIds.contains(stackId)
            },
            onStackExpandedChange = { stackId, isExpanded ->
                notesListViewModel.onStackExpandedChange(stackId, isExpanded)
            },
        )

        recyclerViewNotes.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewNotes.adapter = notesAdapter

        /* обработка нажатия по Floating Action Button добавления новой заметки */
        fabAddNote.setOnClickListener {
            noteEditorViewModel.startCreate()
            editorHostViewModel.openCreate()
        }

        recyclerViewNotes.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                notesListViewModel.uiState.collect { state ->
                    notesAdapter.submitList(state.items)
                }
            }
        }
    }
}