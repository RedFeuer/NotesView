package com.example.noteslist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.noteslist.data.local.entity.NoteEntity
import com.example.noteslist.domain.domainModel.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY createdAtMillis DESC")
    fun getNotes() : Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id : Long) : NoteEntity?

    /** возвращает сгенерированный id вставленной заметки */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note : NoteEntity) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes : List<NoteEntity>)

    @Update
    suspend fun updateNote(note : NoteEntity)

    @Query("UPDATE notes SET isViewed = NOT isViewed WHERE id = :id")
    suspend fun toggleViewed(id : Long)

    @Query("SELECT COUNT(*) FROM notes")
    suspend fun getNotesCount() : Int
}