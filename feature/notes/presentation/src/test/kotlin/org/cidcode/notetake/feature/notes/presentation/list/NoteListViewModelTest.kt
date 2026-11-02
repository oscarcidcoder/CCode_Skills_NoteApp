package org.cidcode.notetake.feature.notes.presentation.list

import assertk.assertThat
import assertk.assertions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.cidcode.notetake.core.domain.DataError
import org.cidcode.notetake.core.presentation.UiText
import org.cidcode.notetake.feature.notes.domain.Note
import org.cidcode.notetake.feature.notes.presentation.FakeNoteLocalDataSource
import org.cidcode.notetake.core.presentation.R as CoreR
import org.cidcode.notetake.feature.notes.presentation.R
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteListViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `notes from the data source are mapped to ui models`() = runTest {
        val source = FakeNoteLocalDataSource()
        source.notes.value = listOf(note(id = 1L, title = "Groceries", body = "Milk"))

        val viewModel = NoteListViewModel(source)
        val state = viewModel.awaitLoaded()

        assertThat(state.notes).hasSize(1)
        assertThat(state.notes.first().title).isEqualTo("Groceries")
        assertThat(state.error).isNull()
    }

    @Test
    fun `an empty data source produces an empty list`() = runTest {
        val viewModel = NoteListViewModel(FakeNoteLocalDataSource())

        val state = viewModel.awaitLoaded()

        assertThat(state.notes).isEmpty()
        assertThat(state.showEmptyState).isTrue()
    }

    @Test
    fun `a data source failure surfaces a ui text error`() = runTest {
        val source = FakeNoteLocalDataSource()
        source.errorState.value = DataError.Local.UNKNOWN

        val viewModel = NoteListViewModel(source)
        val state = viewModel.await { it.error != null }

        val error = state.error
        assertThat(error).isNotNull()
        assertThat((error as UiText.StringResource).id).isEqualTo(CoreR.string.error_unknown)
    }

    @Test
    fun `previous notes survive a later data source failure`() = runTest {
        val source = FakeNoteLocalDataSource()
        source.notes.value = listOf(note(id = 1L, title = "Kept"))
        val viewModel = NoteListViewModel(source)
        viewModel.awaitLoaded()

        source.errorState.value = DataError.Local.UNKNOWN
        val state = viewModel.await { it.error != null }

        assertThat(state.notes).hasSize(1)
        assertThat(state.notes.first().title).isEqualTo("Kept")
    }

    @Test
    fun `retry resubscribes after a failure`() = runTest {
        val source = FakeNoteLocalDataSource()
        source.errorState.value = DataError.Local.UNKNOWN
        val viewModel = NoteListViewModel(source)
        viewModel.await { it.error != null }

        source.errorState.value = null
        viewModel.onAction(NoteListAction.OnRetryClick)
        val state = viewModel.await { !it.isLoading && it.error == null }

        assertThat(source.observeCalls).isEqualTo(2)
        assertThat(state.error).isNull()
    }

    @Test
    fun `delete request opens the dialog for the tapped note`() = runTest {
        val source = FakeNoteLocalDataSource()
        source.notes.value = listOf(note(id = 4L, title = "Delete me"))
        val viewModel = NoteListViewModel(source)
        viewModel.awaitLoaded()

        viewModel.onAction(NoteListAction.OnDeleteRequested(4L))

        assertThat(viewModel.state.value.notePendingDeletion?.id).isEqualTo(4L)
    }

    @Test
    fun `delete request for an unknown note keeps the dialog closed`() = runTest {
        val source = FakeNoteLocalDataSource()
        source.notes.value = listOf(note(id = 4L, title = "Known"))
        val viewModel = NoteListViewModel(source)
        viewModel.awaitLoaded()

        viewModel.onAction(NoteListAction.OnDeleteRequested(99L))

        assertThat(viewModel.state.value.notePendingDeletion).isNull()
    }

    @Test
    fun `confirming deletion removes the note and closes the dialog`() = runTest {
        val source = FakeNoteLocalDataSource()
        source.notes.value = listOf(note(id = 4L, title = "Delete me"))
        val viewModel = NoteListViewModel(source)
        viewModel.awaitLoaded()

        viewModel.onAction(NoteListAction.OnDeleteRequested(4L))
        viewModel.onAction(NoteListAction.OnDeleteConfirmed)
        val state = viewModel.await { it.notes.isEmpty() && it.notePendingDeletion == null }

        assertThat(source.deletedIds).containsExactly(4L)
        assertThat(state.notes).isEmpty()
    }

    @Test
    fun `dismissing deletion keeps the note`() = runTest {
        val source = FakeNoteLocalDataSource()
        source.notes.value = listOf(note(id = 4L, title = "Keep me"))
        val viewModel = NoteListViewModel(source)
        viewModel.awaitLoaded()

        viewModel.onAction(NoteListAction.OnDeleteRequested(4L))
        viewModel.onAction(NoteListAction.OnDeleteDismissed)

        assertThat(source.deletedIds).isEmpty()
        assertThat(viewModel.state.value.notes).hasSize(1)
        assertThat(viewModel.state.value.notePendingDeletion).isNull()
    }

    @Test
    fun `a failed deletion surfaces an error and closes the dialog`() = runTest {
        val source = FakeNoteLocalDataSource()
        source.notes.value = listOf(note(id = 4L, title = "Delete me"))
        source.deleteError = DataError.Local.DISK_FULL
        val viewModel = NoteListViewModel(source)
        viewModel.awaitLoaded()

        viewModel.onAction(NoteListAction.OnDeleteRequested(4L))
        viewModel.onAction(NoteListAction.OnDeleteConfirmed)
        val state = viewModel.await { it.error != null }

        assertThat(state.notePendingDeletion).isNull()
        assertThat(state.notes).hasSize(1)
        assertThat((state.error as UiText.StringResource).id).isEqualTo(CoreR.string.error_disk_full)
    }

    private suspend fun NoteListViewModel.awaitLoaded(): NoteListState =
        await { !it.isLoading }

    private suspend fun NoteListViewModel.await(
        predicate: (NoteListState) -> Boolean
    ): NoteListState = state.first(predicate)

    private fun note(
        id: Long,
        title: String,
        body: String = ""
    ): Note = Note(id = id, title = title, body = body, updatedAt = 0L)
}
