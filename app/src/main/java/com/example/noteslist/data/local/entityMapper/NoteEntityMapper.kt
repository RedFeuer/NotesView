package com.example.noteslist.data.local.entityMapper

import com.example.noteslist.data.local.entity.NoteEntity
import com.example.noteslist.domain.domainModel.Note
import javax.inject.Inject

class NoteEntityMapper @Inject constructor() {
    fun toDomain(entity : NoteEntity) : Note {
        return Note(
            id = entity.id,
            title = entity.title,
            isImportant = entity.isImportant,
            isViewed = entity.isViewed,
            description = entity.description,
            createdAtMillis = entity.createdAtMillis,
        )
    }

    fun toEntity(domainModel: Note) : NoteEntity {
        return NoteEntity(
            id = domainModel.id,
            title = domainModel.title,
            isImportant = domainModel.isImportant,
            isViewed = domainModel.isViewed,
            description = domainModel.description,
            createdAtMillis = domainModel.createdAtMillis,
        )
    }
}