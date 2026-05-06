package com.example.noteslist.di.module

import com.example.noteslist.di.subcomponent.MainActivityComponent
import com.example.noteslist.di.subcomponent.SettingsBottomSheetComponent
import dagger.Module

/* вынесены в сабкомпонент, так как отличается жизненный цикл */
@Module(
    subcomponents = [
        MainActivityComponent::class,
    ]
)
object SubcomponentsModule