package com.example.notesapp.feature.notes.data

import com.example.notesapp.core.domain.Result
import com.example.notesapp.feature.notes.domain.Note
import com.example.notesapp.feature.notes.domain.NoteError
import com.example.notesapp.feature.notes.domain.NoteRepository
import kotlinx.coroutines.flow.Flow

class OfflineFirstNoteRepository(
    private val localDataSource: NoteLocalDataSource
) : NoteRepository {

    override fun getNotes(): Flow<Result<List<Note>, NoteError>> {
        return localDataSource.getNotes()
    }

    override suspend fun getNote(id: String): Result<Note, NoteError> {
        return localDataSource.getNote(id)
    }

    override suspend fun saveNote(note: Note): Result<Unit, NoteError> {
        return localDataSource.saveNote(note)
    }

    override suspend fun deleteNote(id: String): Result<Unit, NoteError> {
        return localDataSource.deleteNote(id)
    }
}