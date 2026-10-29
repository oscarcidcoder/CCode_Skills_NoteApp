package com.example.notesapp.feature.notes.data

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val notesDataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            NotesDatabase::class.java,
            NotesDatabase.DATABASE_NAME
        ).build()
    }

    single { get<NotesDatabase>().noteDao() }

    singleOf(::RoomNoteDataSource) { bind<NoteLocalDataSource>() }
    singleOf(::OfflineFirstNoteRepository) { bind<NoteRepository>() }
}