package com.example.noteslist.di.module

import com.example.noteslist.data.repositoryImpl.NotesRepositoryImpl
import com.example.noteslist.di.scope.AppScope
import com.example.noteslist.domain.repository.NotesRepository
import dagger.Binds
import dagger.Module

@Module
interface RepositoryModule {
    @Binds
    @AppScope
    fun bindNotesRepository(impl: NotesRepositoryImpl) : NotesRepository
}