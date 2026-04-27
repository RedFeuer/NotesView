package com.example.noteslist.presentation.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.R
import com.example.noteslist.data.repositoryImpl.NotesRepositoryImpl
import com.example.noteslist.presentation.editor.NoteEditorActivity
import com.example.noteslist.presentation.notes.adapters.NotesListAdapter
import com.example.noteslist.presentation.notes.toNoteListItems
import com.google.android.material.floatingactionbutton.FloatingActionButton

//тут будешь ваша активити
class MainActivity : AppCompatActivity() {
    /** Singleton репозитория для актуальности заметок */
    private val repository = NotesRepositoryImpl.instance
    private lateinit var notesAdapter : NotesListAdapter
    /** множество раскрытых стеков заметок, где идентификатор - id стека*/
    private var expandedStackIds = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val rootView = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerViewNotes = findViewById<RecyclerView>(R.id.recyclerViewNotes)
        recyclerViewNotes.layoutManager = LinearLayoutManager(this)
        notesAdapter = NotesListAdapter(
            onNoteClick = { note ->
                startActivity(NoteEditorActivity.createEditIntent(this, note.uiId))
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
            }
        )
        recyclerViewNotes.adapter = notesAdapter

        /* обработка нажатия по Floating Action Button добавления новой заметки */
        val fabAddNote = findViewById<FloatingActionButton>(R.id.fabAddNote)
        fabAddNote.setOnClickListener {
            startActivity(NoteEditorActivity.createAddIntent(this))
//            Toast.makeText(this, "Переход на экран создания заметки", Toast.LENGTH_SHORT).show()
        }

        /* обработка скрытия Floating Action Button добавления новой заметки при скролле
        * и возвращения кнопки при окончании скролла*/
        recyclerViewNotes.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) {
                    fabAddNote.hide()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    /* post чтобы анимация корректнее отображалась при быстрых скроллах туда-сюда */
                    fabAddNote.post { fabAddNote.show() }
                }
            }
        })

        renderNotes()
    }

    /* при возврате к Activity после другой Activity. Например, после NoteEditorActivity*/
    override fun onResume() {
        super.onResume()
        renderNotes()
    }

    /** прикрепление списка заметок к экрану приложения */
    private fun renderNotes() {
        notesAdapter.submitList(repository.getNotes().toNoteListItems())
    }
}