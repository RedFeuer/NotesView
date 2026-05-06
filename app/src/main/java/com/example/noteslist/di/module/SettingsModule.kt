package com.example.noteslist.di.module

import com.example.noteslist.data.repositoryImpl.SettingsRepositoryImpl
import com.example.noteslist.di.scope.AppScope
import com.example.noteslist.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module

@Module
interface SettingsModule {
    @Binds
    @AppScope
    fun bindSettingsRepository(impl: SettingsRepositoryImpl) : SettingsRepository
}