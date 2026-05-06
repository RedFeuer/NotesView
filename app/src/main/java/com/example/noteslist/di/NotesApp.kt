package com.example.noteslist.di

import android.app.Application
import com.example.noteslist.di.component.AppComponent
import com.example.noteslist.di.component.DaggerAppComponent
import com.example.noteslist.domain.useCase.EnsureDemoNotesUseCase
import javax.inject.Inject

class NotesApp : Application() {
    lateinit var appComponent: AppComponent
        private set

    @Inject
    lateinit var ensureDemoNotesUseCase: EnsureDemoNotesUseCase

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.factory()
            .create(this)

        appComponent.inject(this)

        /* сохраняем демо-заметки в БД при первом запуске */
        ensureDemoNotesUseCase.launch()
    }
}