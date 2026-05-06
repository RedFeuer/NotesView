package com.example.noteslist.di.subcomponent

import com.example.noteslist.di.scope.FragmentScope
import com.example.noteslist.presentation.list.NotesListFragment
import com.example.noteslist.presentation.settings.SettingsBottomSheetFragment
import dagger.Subcomponent

@Subcomponent
@FragmentScope
interface SettingsBottomSheetComponent {
    fun inject(fragment: SettingsBottomSheetFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create() : SettingsBottomSheetComponent
    }
}