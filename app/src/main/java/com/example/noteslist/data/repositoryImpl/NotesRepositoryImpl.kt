package com.example.noteslist.data.repositoryImpl

import com.example.noteslist.data.local.dao.NoteDao
import com.example.noteslist.data.local.entityMapper.NoteEntityMapper
import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val noteEntityMapper: NoteEntityMapper,
) : NotesRepository {

    override fun getNotes(): Flow<List<Note>> {
        return noteDao.getNotes()
            .map { entities ->
                entities.map{ entity -> noteEntityMapper.toDomain(entity) }
            }
    }

    override suspend fun getNoteById(id : Long): Note? {
        return noteDao.getNoteById(id)?.let { entity ->
            noteEntityMapper.toDomain(entity)
        }
    }

    override suspend fun addNote(note: Note) {
        noteDao.insertNote(noteEntityMapper.toEntity(note))
    }

    override suspend fun addNotes(notes: List<Note>) {
        noteDao.insertNotes(
            notes.map { note -> noteEntityMapper.toEntity(note) }
        )
    }

    override suspend fun updateNote(note: Note) {
        noteDao.updateNote(noteEntityMapper.toEntity(note))
    }

    override suspend fun toggleViewed(id : Long) {
        noteDao.toggleViewed(id)
    }

    override suspend fun isEmpty(): Boolean {
        return noteDao.getNotesCount() == 0
    }
}