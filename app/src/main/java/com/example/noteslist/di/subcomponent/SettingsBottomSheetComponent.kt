package com.example.noteslist.di.subcomponent

import com.example.noteslist.di.scope.FragmentScope
import com.example.noteslist.presentation.list.NotesListFragment
import dagger.Subcomponent

@Subcomponent
@FragmentScope
interface SettingsBottomSheetComponent {
    fun inject(fragment: SettingsBottomSheetComponent)

    @Subcomponent.Factory
    interface Factory {
        fun create() : SettingsBottomSheetComponent
    }
}