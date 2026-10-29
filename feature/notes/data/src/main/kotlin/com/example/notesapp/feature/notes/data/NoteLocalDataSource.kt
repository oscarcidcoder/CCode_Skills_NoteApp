package com.example.notesapp.feature.notes.data

import com.example.notesapp.core.domain.Error
import com.example.notesapp.core.domain.Result
import com.example.notesapp.feature.notes.domain.Note
import com.example.notesapp.feature.notes.domain.NoteError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

interface NoteLocalDataSource {
    fun getNotes(): Flow<Result<List<Note>, NoteError>>
    suspend fun getNote(id: String): Result<Note, NoteError>
    suspend fun saveNote(note: Note): Result<Unit, NoteError>
    suspend fun deleteNote(id: String): Result<Unit, NoteError>
}

class RoomNoteDataSource(
    private val dao: NoteDao
) : NoteLocalDataSource {

    override fun getNotes(): Flow<Result<List<Note>, NoteError>> {
        return dao.getAllNotes()
            .map { entities ->
                Result.Success(entities.map { it.toNote() })
            }
            .catch {
                emit(Result.Error(NoteError.UNKNOWN))
            }
    }

    override suspend fun getNote(id: String): Result<Note, NoteError> {
        return try {
            val entity = dao.getNote(id)
            if (entity != null) {
                Result.Success(entity.toNote())
            } else {
                Result.Error(NoteError.NOT_FOUND)
            }
        } catch (e: Exception) {
            Result.Error(NoteError.UNKNOWN)
        }
    }

    override suspend fun saveNote(note: Note): Result<Unit, NoteError> {
        return try {
            dao.insertNote(note.toNoteEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(NoteError.UNKNOWN)
        }
    }

    override suspend fun deleteNote(id: String): Result<Unit, NoteError> {
        return try {
            val deleted = dao.deleteNoteById(id)
            if (deleted > 0) {
                Result.Success(Unit)
            } else {
                Result.Error(NoteError.NOT_FOUND)
            }
        } catch (e: Exception) {
            Result.Error(NoteError.UNKNOWN)
        }
    }
}