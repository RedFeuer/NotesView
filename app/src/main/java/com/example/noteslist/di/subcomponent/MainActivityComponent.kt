package com.example.noteslist.di.subcomponent

import com.example.noteslist.di.scope.ActivityScope
import com.example.noteslist.presentation.view.MainActivity
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface MainActivityComponent {
    fun inject(activity: MainActivity)

    fun notesListFragmentComponentFactory() : NotesListFragmentComponent.Factory

    @Subcomponent.Factory
    interface Factory {
        fun create() : MainActivityComponent
    }
}