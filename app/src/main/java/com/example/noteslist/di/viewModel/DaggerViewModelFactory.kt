package com.example.noteslist.di.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class DaggerViewModelFactory @Inject constructor(
    /** мапа : <ViewModel, которую хотим получить; Поставщик, создающий нужный экземпляр> */
    private val creators : Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass : Class<T>) : T {
        val creator = creators[modelClass]
            ?: creators.entries.firstOrNull { modelClass.isAssignableFrom(it.key) }?.value
            ?: throw IllegalArgumentException("Unknown ViewModel class")

        @Suppress("UNCHECKED CAST")
        return creator.get() as T
    }
}