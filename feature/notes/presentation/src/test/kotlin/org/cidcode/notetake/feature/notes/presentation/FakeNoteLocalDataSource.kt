package org.cidcode.notetake.feature.notes.presentation

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import org.cidcode.notetake.core.domain.DataError
import org.cidcode.notetake.core.domain.EmptyResult
import org.cidcode.notetake.core.domain.Result
import org.cidcode.notetake.feature.notes.domain.NEW_NOTE_ID
import org.cidcode.notetake.feature.notes.domain.Note
import org.cidcode.notetake.feature.notes.domain.NoteLocalDataSource

class FakeNoteLocalDataSource : NoteLocalDataSource {

    val notes = MutableStateFlow<List<Note>>(emptyList())
    val errorState = MutableStateFlow<DataError.Local?>(null)

    var getNoteResult: Result<Note?, DataError.Local>? = null
    var upsertError: DataError.Local? = null
    var deleteError: DataError.Local? = null
    var upsertGate: CompletableDeferred<Unit>? = null

    var observeCalls: Int = 0
        private set
    var getNoteCalls: Int = 0
        private set

    val upsertedNotes = mutableListOf<Note>()
    val deletedIds = mutableListOf<Long>()

    private var nextId = 1L

    override fun observeNotes(): Flow<Result<List<Note>, DataError.Local>> {
        observeCalls += 1
        return combine<List<Note>, DataError.Local?, Result<List<Note>, DataError.Local>>(
            notes,
            errorState
        ) { currentNotes, error -> toResult(currentNotes, error) }
    }

    override suspend fun getNote(id: Long): Result<Note?, DataError.Local> {
        getNoteCalls += 1
        getNoteResult?.let { return it }
        return Result.Success(notes.value.firstOrNull { it.id == id })
    }

    override suspend fun upsertNote(note: Note): EmptyResult<DataError.Local> {
        upsertGate?.await()

        val error = upsertError
        if (error != null) return Result.Error(error)

        val stored = if (note.id == NEW_NOTE_ID) note.copy(id = nextId++) else note
        upsertedNotes += stored
        notes.value = notes.value.filterNot { it.id == stored.id } + stored
        return Result.Success(Unit)
    }

    override suspend fun deleteNote(id: Long): EmptyResult<DataError.Local> {
        val error = deleteError
        if (error != null) return Result.Error(error)

        deletedIds += id
        notes.value = notes.value.filterNot { it.id == id }
        return Result.Success(Unit)
    }

    private fun toResult(
        currentNotes: List<Note>,
        error: DataError.Local?
    ): Result<List<Note>, DataError.Local> = if (error != null) {
        Result.Error(error)
    } else {
        Result.Success(currentNotes)
    }
}
