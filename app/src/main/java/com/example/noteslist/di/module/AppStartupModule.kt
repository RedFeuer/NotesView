package com.example.noteslist.di.module

import com.example.noteslist.data.repositoryImpl.AppStartupRepositoryImpl
import com.example.noteslist.di.scope.AppScope
import com.example.noteslist.domain.repository.AppStartupRepository
import dagger.Binds
import dagger.Module

@Module
interface AppStartupModule {
    @Binds
    @AppScope
    fun bindAppStartupRepository(impl: AppStartupRepositoryImpl) : AppStartupRepository
}