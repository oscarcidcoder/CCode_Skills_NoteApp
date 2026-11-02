package org.cidcode.notetake.feature.notes.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import org.cidcode.notetake.feature.notes.data.RoomNoteDataSource
import org.cidcode.notetake.feature.notes.domain.NoteLocalDataSource

@Module
@InstallIn(SingletonComponent::class)
abstract class NotesDataModule {

    @Binds
    @Singleton
    abstract fun bindNoteLocalDataSource(impl: RoomNoteDataSource): NoteLocalDataSource
}
