package com.example.noteslist.di

import android.app.Application
import com.example.noteslist.di.component.AppComponent

class NotesApp : Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.factory()
            .create(this)
    }
}