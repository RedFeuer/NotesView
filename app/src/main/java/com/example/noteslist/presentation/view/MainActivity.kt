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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
    }
}