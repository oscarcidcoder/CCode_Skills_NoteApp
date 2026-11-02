package org.cidcode.notetake.feature.notes.data

import android.database.sqlite.SQLiteFullException
import java.util.concurrent.CancellationException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.cidcode.notetake.core.domain.DataError
import org.cidcode.notetake.core.domain.EmptyResult
import org.cidcode.notetake.core.domain.Result
import org.cidcode.notetake.feature.notes.domain.Note
import org.cidcode.notetake.feature.notes.domain.NoteLocalDataSource

class RoomNoteDataSource @Inject constructor(
    private val noteDao: NoteDao
) : NoteLocalDataSource {

    override fun observeNotes(): Flow<Result<List<Note>, DataError.Local>> =
        noteDao.observeNotes()
            .map { entities -> toNotesResult(entities) }
            .catch { throwable ->
                if (throwable is CancellationException) throw throwable
                emit(Result.Error(DataError.Local.UNKNOWN))
            }

    override suspend fun getNote(id: Long): Result<Note?, DataError.Local> =
        runCatchingDatabase { noteDao.getNote(id)?.toNote() }

    override suspend fun upsertNote(note: Note): EmptyResult<DataError.Local> =
        runCatchingDatabase { noteDao.upsert(note.toNoteEntity()) }

    override suspend fun deleteNote(id: Long): EmptyResult<DataError.Local> =
        runCatchingDatabase { noteDao.delete(id) }
}

private fun toNotesResult(entities: List<NoteEntity>): Result<List<Note>, DataError.Local> =
    Result.Success(entities.map(NoteEntity::toNote))

private inline fun <T> runCatchingDatabase(block: () -> T): Result<T, DataError.Local> =
    try {
        Result.Success(block())
    } catch (exception: SQLiteFullException) {
        Result.Error(DataError.Local.DISK_FULL)
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        Result.Error(DataError.Local.UNKNOWN)
    }
