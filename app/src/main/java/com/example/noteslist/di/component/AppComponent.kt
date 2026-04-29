package com.example.noteslist.di.component

import android.app.Application
import com.example.noteslist.di.NotesApp
import com.example.noteslist.di.coroutine.module.CoroutineModule
import com.example.noteslist.di.module.DatabaseModule
import com.example.noteslist.di.module.DemoNotesModule
import com.example.noteslist.di.module.RepositoryModule
import com.example.noteslist.di.module.SubcomponentsModule
import com.example.noteslist.di.module.ViewModelModule
import com.example.noteslist.di.scope.AppScope
import com.example.noteslist.di.subcomponent.MainActivityComponent
import dagger.BindsInstance
import dagger.Component

@AppScope
@Component(
    modules = [
        RepositoryModule::class,
        ViewModelModule::class,
        SubcomponentsModule::class,
        CoroutineModule::class,
        DatabaseModule::class,
        DemoNotesModule::class,
    ]
)
interface AppComponent {
    fun inject(app: NotesApp)
    fun mainActivityComponentFactory() : MainActivityComponent.Factory

    @Component.Factory
    interface Factory{
        fun create(
            @BindsInstance application: Application,
        ) : AppComponent
    }
}