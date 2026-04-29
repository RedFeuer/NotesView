package com.example.noteslist.di.module

import android.app.Application
import androidx.room.Room
import com.example.noteslist.data.local.dao.NoteDao
import com.example.noteslist.data.local.database.NotesDatabase
import com.example.noteslist.di.scope.AppScope
import dagger.Module
import dagger.Provides

@Module
object DatabaseModule {

    @Provides
    @AppScope
    fun provideNotesDatabase(application : Application) : NotesDatabase {
        return Room.databaseBuilder(
            application,
            NotesDatabase::class.java,
            NotesDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideNoteDao(
        notesDatabase: NotesDatabase,
    ) : NoteDao {
        return notesDatabase.noteDao()
    }
}