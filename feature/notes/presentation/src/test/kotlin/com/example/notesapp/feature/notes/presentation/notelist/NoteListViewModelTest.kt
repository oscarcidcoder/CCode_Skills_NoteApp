package com.example.notesapp.feature.notes.presentation.notelist

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.example.notesapp.feature.notes.domain.NoteError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
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
    fun `collecting notes emits them as ui models`() = runTest(testDispatcher) {
        val repo = FakeNoteRepository()
        repo.setNotes(listOf(note(id = "1", title = "First"), note(id = "2", title = "Second")))

        val viewModel = NoteListViewModel(repo)

        viewModel.state.test {
            val final = awaitItem()
            assertThat(final.notes).hasSize(2)
            assertThat(final.isLoading).isFalse()
        }
    }

    @Test
    fun `empty repository emits empty list without error`() = runTest(testDispatcher) {
        val repo = FakeNoteRepository()
        repo.setNotes(emptyList())

        val viewModel = NoteListViewModel(repo)

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.notes).isEmpty()
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `deleting a note triggers repository delete and keeps going`() = runTest(testDispatcher) {
        val repo = FakeNoteRepository()
        repo.setNotes(listOf(note(id = "1", title = "To delete")))

        val viewModel = NoteListViewModel(repo)

        viewModel.state.test {
            awaitItem() // initial load
            viewModel.onAction(NoteListAction.OnDeleteNote("1"))
            assertThat(repo.deleteNoteCalls).isEqualTo(1)
            assertThat(repo.deletedNoteIds).containsExactly("1")
        }
    }

    @Test
    fun `delete failure surfaces error`() = runTest(testDispatcher) {
        val repo = FakeNoteRepository()
        repo.deleteNoteResult = com.example.notesapp.core.domain.Result.Error(NoteError.UNKNOWN)
        repo.setNotes(listOf(note(id = "1")))

        val viewModel = NoteListViewModel(repo)

        viewModel.state.test {
            awaitItem() // initial
            viewModel.onAction(NoteListAction.OnDeleteNote("1"))
            val state = awaitItem()
            assertThat(state.error).isNotNull()
        }
    }
}