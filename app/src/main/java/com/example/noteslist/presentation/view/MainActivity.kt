package com.example.noteslist.presentation.view

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.R
import com.example.noteslist.data.repositoryImpl.NotesRepositoryImpl
import com.example.noteslist.presentation.notes.adapters.NotesListAdapter
import com.example.noteslist.presentation.notes.toNoteListItems

//тут будешь ваша активити
class MainActivity : AppCompatActivity() {
    private val repository = NotesRepositoryImpl.instance // Singleton для актуальности заметок
    private val notesAdapter = NotesListAdapter()

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
        recyclerViewNotes.adapter = notesAdapter

        renderNotes()
    }

    /* при возврате к Activity после другой Activity. Например, после NoteEditorActivity*/
    override fun onResume() {
        super.onResume()
        renderNotes()
    }

    private fun renderNotes() {
        notesAdapter.submitList(repository.getNotes().toNoteListItems())
    }
}