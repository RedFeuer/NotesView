package com.example.noteslist.di.module

import com.example.noteslist.data.demo.DemoNotesSourceImpl
import com.example.noteslist.domain.demo.DemoNotesSource
import dagger.Binds
import dagger.Module

@Module
interface DemoNotesModule {
    @Binds
    fun bindDemoNotesSource(impl: DemoNotesSourceImpl) : DemoNotesSource
}