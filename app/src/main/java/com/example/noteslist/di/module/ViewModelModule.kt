package com.example.noteslist.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.noteslist.di.viewModel.DaggerViewModelFactory
import com.example.noteslist.di.viewModel.ViewModelKey
import com.example.noteslist.presentation.viewModel.EditorHostViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {
    @Binds
    fun bindViewModelFactory(factory: DaggerViewModelFactory) : ViewModelProvider.Factory

    /** биндим EditorHostViewModel в мапу Multibinding как ViewModel */
    @Binds
    @IntoMap
    @ViewModelKey(EditorHostViewModel::class)
    fun bindEditorHostViewModel(viewModel: EditorHostViewModel): ViewModel
}