package com.example.notesapp.feature.notes.domain

import com.example.notesapp.core.domain.EmptyResult
import com.example.notesapp.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotes(): Flow<Result<List<Note>, NoteError>>
    suspend fun getNote(id: String): Result<Note, NoteError>
    suspend fun saveNote(note: Note): EmptyResult<NoteError>
    suspend fun deleteNote(id: String): EmptyResult<NoteError>
}