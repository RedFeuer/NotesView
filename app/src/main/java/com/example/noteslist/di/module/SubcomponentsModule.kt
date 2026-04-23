package com.example.noteslist.di.module

import com.example.noteslist.di.subcomponent.MainActivityComponent
import com.example.noteslist.di.subcomponent.NotesListFragmentComponent
import dagger.Module

@Module(
    subcomponents = [
        MainActivityComponent::class,
        NotesListFragmentComponent::class,
    ]
)
object SubcomponentsModule