package com.example.noteslist.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.noteslist.di.viewModel.DaggerViewModelFactory
import com.example.noteslist.di.viewModel.ViewModelKey
import com.example.noteslist.presentation.viewModel.EditorHostViewModel
import com.example.noteslist.presentation.viewModel.NoteEditorViewModel
import com.example.noteslist.presentation.viewModel.NotesListViewModel
import com.example.noteslist.presentation.viewModel.SettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {
    @Binds
    fun bindViewModelFactory(factory: DaggerViewModelFactory) : ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    fun provideSettingsViewModel(viewModel : SettingsViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotesListViewModel::class)
    fun provideNotesListViewModel(viewModel: NotesListViewModel) : ViewModel

    /** биндим EditorHostViewModel в мапу Multibinding как ViewModel */
    @Binds
    @IntoMap
    @ViewModelKey(EditorHostViewModel::class)
    fun bindEditorHostViewModel(viewModel: EditorHostViewModel): ViewModel

    /** биндим NoteEditorViewModel в мапу Multibinding как ViewModel */
    @Binds
    @IntoMap
    @ViewModelKey(NoteEditorViewModel::class)
    fun bindNoteEditorViewModel(viewModel : NoteEditorViewModel) : ViewModel
}