package com.example.notesapp.feature.notes.presentation.notelist

import com.example.notesapp.core.domain.Result
import com.example.notesapp.feature.notes.domain.Note
import com.example.notesapp.feature.notes.domain.NoteError
import com.example.notesapp.feature.notes.domain.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Instant

class FakeNoteRepository : NoteRepository {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notesFlow: StateFlow<List<Note>> = _notes.asStateFlow()

    var getNoteResult: Result<Note, NoteError> = Result.Error(NoteError.NOT_FOUND)
    var saveNoteResult: Result<Unit, NoteError> = Result.Success(Unit)
    var deleteNoteResult: Result<Unit, NoteError> = Result.Success(Unit)

    var getNoteCalls = 0
    var saveNoteCalls = 0
    var deleteNoteCalls = 0

    val savedNotes = mutableListOf<Note>()
    val deletedNoteIds = mutableListOf<String>()

    fun setNotes(notes: List<Note>) {
        _notes.value = notes
    }

    override fun getNotes(): Flow<Result<List<Note>, NoteError>> {
        return kotlinx.coroutines.flow.flow {
            emit(Result.Success(_notes.value))
        }
    }

    override suspend fun getNote(id: String): Result<Note, NoteError> {
        getNoteCalls++
        return getNoteResult
    }

    override suspend fun saveNote(note: Note): Result<Unit, NoteError> {
        saveNoteCalls++
        savedNotes += note
        return saveNoteResult
    }

    override suspend fun deleteNote(id: String): Result<Unit, NoteError> {
        deleteNoteCalls++
        deletedNoteIds += id
        return deleteNoteResult
    }
}

fun note(
    id: String = "1",
    title: String = "Meeting notes",
    content: String = "Discussed roadmap",
    createdAt: Instant = Instant.fromEpochMilliseconds(1_700_000_000_000L),
    updatedAt: Instant = Instant.fromEpochMilliseconds(1_700_000_000_000L)
) = Note(id = id, title = title, content = content, createdAt = createdAt, updatedAt = updatedAt)