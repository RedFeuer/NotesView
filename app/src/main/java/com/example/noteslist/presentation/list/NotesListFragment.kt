package com.example.noteslist.presentation.list

//import com.example.noteslist.presentation.editor.NoteEditorActivity
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.R
import com.example.noteslist.data.repositoryImpl.NotesRepositoryImpl
import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.presentation.editor.NoteEditorFragment
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

    companion object {
        private const val NOTE_EDITOR_RESULT_KEY = "note_editor_result"
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
                openEditor(note)
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
            openEditor(null)
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

        requireActivity().supportFragmentManager.setFragmentResultListener(
            NOTE_EDITOR_RESULT_KEY,
            viewLifecycleOwner
        ) { _, _ ->
            renderNotes()
        }
    }

    /** проверяем, есть ли в текущем layout правый контейнер detail_fragment_container
     * по сути проверка, что мы в ландшафтном режиме ориентации*/
    private fun isTwoPane() : Boolean {
        return requireActivity().findViewById<View?>(R.id.detail_fragment_container) != null
    }

    private fun openEditor(note : Note?) {
        if (isTwoPane()) {
            /* ландшафтный сплит-экран */
            val args = bundleOf(
                "note" to note,
                "isEditMode" to (note != null)
            )

            requireActivity().supportFragmentManager.commit {
                setReorderingAllowed(true)
                /* в контейнер detail_fragment_container кладем NoteEditorFragment  */
                replace(R.id.detail_fragment_container, NoteEditorFragment::class.java, args)
            }
        } else {
            /* портретный экран */
            val direction =
                NotesListFragmentDirections.actionNotesListFragmentToNoteEditorFragment(
                    note = note,
                    isEditMode = note != null,
                )
            findNavController().navigate(direction)
        }
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