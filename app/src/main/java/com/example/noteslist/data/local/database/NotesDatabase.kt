package com.example.noteslist.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.noteslist.data.local.dao.NoteDao
import com.example.noteslist.data.local.entity.NoteEntity

@Database(
    entities = [NoteEntity::class],
    version = 1,
)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        const val DATABASE_NAME = "notes_database"
    }
}