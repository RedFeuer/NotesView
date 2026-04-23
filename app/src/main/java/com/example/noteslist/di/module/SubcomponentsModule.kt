package com.example.noteslist.di.module

import com.example.noteslist.di.subcomponent.MainActivityComponent
import com.example.noteslist.di.subcomponent.NotesListFragmentComponent
import dagger.Module

/* вынесены в сабкомпонент, так как отличается жизненный цикл */
@Module(
    subcomponents = [
        MainActivityComponent::class,
        NotesListFragmentComponent::class,
    ]
)
object SubcomponentsModule