package com.example.notesapp

import android.app.Application
import com.example.notesapp.feature.notes.data.notesDataModule
import com.example.notesapp.feature.notes.presentation.notesPresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NotesApplication)
            modules(
                notesDataModule,
                notesPresentationModule
            )
        }
    }
}