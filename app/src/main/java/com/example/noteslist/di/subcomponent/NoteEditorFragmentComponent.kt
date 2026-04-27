package com.example.noteslist.di.subcomponent

import com.example.noteslist.di.scope.FragmentScope
import com.example.noteslist.presentation.editor.NoteEditorFragment
import dagger.Subcomponent

@FragmentScope
@Subcomponent
interface NoteEditorFragmentComponent {
    fun inject(fragment: NoteEditorFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create() : NoteEditorFragmentComponent
    }
}