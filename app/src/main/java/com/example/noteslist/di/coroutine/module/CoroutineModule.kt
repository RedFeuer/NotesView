package com.example.noteslist.di.coroutine.module

import com.example.noteslist.di.coroutine.qualifier.ApplicationCoroutineQualifier
import com.example.noteslist.di.coroutine.qualifier.IoDispatcher
import com.example.noteslist.di.scope.AppScope
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
object CoroutineModule {
    @Provides
    @IoDispatcher
    fun provideIoDispatcher() : CoroutineDispatcher = Dispatchers.IO

    @Provides
    @AppScope
    @ApplicationCoroutineQualifier
    fun provideApplicationScope() : CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default + CoroutineName("Application Scope"))
    }
}