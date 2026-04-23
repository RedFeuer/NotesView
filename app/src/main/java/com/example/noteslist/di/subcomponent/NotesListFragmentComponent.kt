package com.example.noteslist.di.subcomponent

import com.example.noteslist.di.scope.FragmentScope
import com.example.noteslist.presentation.list.NotesListFragment
import dagger.Subcomponent

@FragmentScope
@Subcomponent
interface NotesListFragmentComponent {
    fun inject(fragment : NotesListFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(): NotesListFragmentComponent
    }
}