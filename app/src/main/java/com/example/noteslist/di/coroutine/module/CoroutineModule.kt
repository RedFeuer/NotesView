package com.example.noteslist.di.coroutine.module

import com.example.noteslist.di.coroutine.qualifier.ApplicationCoroutineScope
import com.example.noteslist.di.coroutine.qualifier.DefaultDispatcher
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
    @DefaultDispatcher
    fun provideDefaultDispatcher() : CoroutineDispatcher = Dispatchers.Default

    @Provides
    @AppScope
    @ApplicationCoroutineScope
    fun provideApplicationScope() : CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default + CoroutineName("Application Scope"))
    }
}