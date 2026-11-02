package org.cidcode.notetake.feature.notes.domain

import kotlinx.coroutines.flow.Flow
import org.cidcode.notetake.core.domain.DataError
import org.cidcode.notetake.core.domain.EmptyResult
import org.cidcode.notetake.core.domain.Result

/**
 * Single source of truth for notes. Backed by the local database.
 */
interface NoteLocalDataSource {
    fun observeNotes(): Flow<Result<List<Note>, DataError.Local>>
    suspend fun getNote(id: Long): Result<Note?, DataError.Local>
    suspend fun upsertNote(note: Note): EmptyResult<DataError.Local>
    suspend fun deleteNote(id: Long): EmptyResult<DataError.Local>
}
