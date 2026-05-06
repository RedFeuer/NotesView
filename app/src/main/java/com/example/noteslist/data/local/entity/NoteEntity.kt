package com.example.noteslist.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity (
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,
    val title : String,
    val isImportant : Boolean,
    val isViewed : Boolean,
    val description: String?,
    val createdAtMillis : Long,
)