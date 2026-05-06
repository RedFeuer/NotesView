package com.example.noteslist.presentation.list

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.R
import com.example.noteslist.presentation.notes.adapters.NotesListAdapter
import com.example.noteslist.presentation.settings.SettingsBottomSheetFragment
import com.example.noteslist.presentation.view.MainActivity
import com.example.noteslist.presentation.view.ShimmerNotesListView
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

    /** виджет поиска заметок */
    private lateinit var searchViewNotes : SearchView

    /** кнопка настроек */
    private lateinit var buttonSettings : ImageButton

    /** шиммер */
    private lateinit var shimmerNotes : ShimmerNotesListView

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
        searchViewNotes = view.findViewById<SearchView>(R.id.search_view_notes)
        buttonSettings = view.findViewById(R.id.button_settings)
        shimmerNotes = view.findViewById(R.id.shimmer_notes)

        notesAdapter = NotesListAdapter(
            /* обработка клика - редактирование заметки */
            onNoteClick = { note ->
                noteEditorViewModel.startEdit(note.id)
                editorHostViewModel.openEdit(note.id)
            },
            onNoteLongClick = { note ->
                notesListViewModel.onNoteLongClick(note.id)
            },
            isStackExpanded = { stackId ->
                notesListViewModel.uiState.value.expandedStackIds.contains(stackId)
            },
            onStackExpandedChange = { stackId, isExpanded ->
                notesListViewModel.onStackExpandedChange(stackId, isExpanded)
            },
        )

        searchViewNotes.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    notesListViewModel.onSearchQueryChanged(query.orEmpty())
                    searchViewNotes.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    notesListViewModel.onSearchQueryChanged(newText.orEmpty())
                    return true
                }
            }
        )

        buttonSettings.setOnClickListener {
            SettingsBottomSheetFragment()
                .show(parentFragmentManager, SettingsBottomSheetFragment.TAG)
        }

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

    override fun onDestroyView() {
        recyclerViewNotes.adapter = null
        super.onDestroyView()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                notesListViewModel.uiState.collect { state ->
                    val isShimmerVisible = state.isInitialShimmerVisible

                    shimmerNotes.isVisible = isShimmerVisible
                    recyclerViewNotes.isVisible = !isShimmerVisible
                    fabAddNote.isVisible = !isShimmerVisible

                    /* берем сохраненный запрос из NotesListViewModel и сохраняем в SearchView */
                    if (searchViewNotes.query.toString() != state.searchQuery) {
                        searchViewNotes.setQuery(state.searchQuery, false)
                    }

                    if (!isShimmerVisible) {
                        notesAdapter.submitStackSettings(state.stackSettings)
                        notesAdapter.submitList(state.items)
                    }
                }
            }
        }
    }
}