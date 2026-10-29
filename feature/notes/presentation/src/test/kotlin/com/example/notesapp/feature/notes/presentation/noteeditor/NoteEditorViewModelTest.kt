package com.example.notesapp.feature.notes.presentation.noteeditor

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import androidx.lifecycle.SavedStateHandle
import com.example.notesapp.core.domain.Result
import com.example.notesapp.feature.notes.presentation.notelist.FakeNoteRepository
import com.example.notesapp.feature.notes.presentation.notelist.note
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
class NoteEditorViewModelTest {

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
    fun `new note starts with empty state and isNewNote true`() = runTest(testDispatcher) {
        val handle = SavedStateHandle(mapOf("noteId" to null))
        val viewModel = NoteEditorViewModel(null, handle, FakeNoteRepository())

        val state = viewModel.state.value
        assertThat(state.title).isEqualTo("")
        assertThat(state.content).isEqualTo("")
        assertThat(state.isNewNote).isTrue()
    }

    @Test
    fun `existing note loads its content on init`() = runTest(testDispatcher) {
        val repo = FakeNoteRepository()
        repo.getNoteResult = Result.Success(note(id = "123", title = "Loaded", content = "Body"))
        val handle = SavedStateHandle()

        val viewModel = NoteEditorViewModel("123", handle, repo)

        val state = viewModel.state.value
        assertThat(state.title).isEqualTo("Loaded")
        assertThat(state.content).isEqualTo("Body")
        assertThat(state.isNewNote).isFalse()
    }

    @Test
    fun `title change updates state and saved state handle`() = runTest(testDispatcher) {
        val handle = SavedStateHandle()
        val viewModel = NoteEditorViewModel(null, handle, FakeNoteRepository())

        viewModel.onAction(NoteEditorAction.OnTitleChange("New title"))

        assertThat(viewModel.state.value.title).isEqualTo("New title")
        assertThat(handle.get<String>("title")).isEqualTo("New title")
    }

    @Test
    fun `saving empty title sets validation error and does not save`() = runTest(testDispatcher) {
        val repo = FakeNoteRepository()
        val handle = SavedStateHandle()
        val viewModel = NoteEditorViewModel(null, handle, repo)

        viewModel.onAction(NoteEditorAction.OnTitleChange("   "))
        viewModel.onAction(NoteEditorAction.OnSaveClick)

        assertThat(viewModel.state.value.error).isNotNull()
        assertThat(repo.saveNoteCalls).isEqualTo(0)
    }

    @Test
    fun `saving valid note emits success event`() = runTest(testDispatcher) {
        val repo = FakeNoteRepository()
        val handle = SavedStateHandle()
        val viewModel = NoteEditorViewModel(null, handle, repo)

        viewModel.onAction(NoteEditorAction.OnTitleChange("Valid title"))
        viewModel.onAction(NoteEditorAction.OnContentChange("Some content"))

        viewModel.events.test {
            viewModel.onAction(NoteEditorAction.OnSaveClick)
            assertThat(awaitItem()).isEqualTo(NoteEditorEvent.OnSaveSuccess)
            assertThat(repo.saveNoteCalls).isEqualTo(1)
        }
    }

    @Test
    fun `saving unicode and emoji content preserves it`() = runTest(testDispatcher) {
        val repo = FakeNoteRepository()
        val handle = SavedStateHandle()
        val viewModel = NoteEditorViewModel(null, handle, repo)

        val emojiTitle = "👨‍👩‍👧‍👦 Family 😀"
        val emojiContent = "Arabic: مرحبا, CJK: 你好, combining: e\u0301"

        viewModel.onAction(NoteEditorAction.OnTitleChange(emojiTitle))
        viewModel.onAction(NoteEditorAction.OnContentChange(emojiContent))

        viewModel.events.test {
            viewModel.onAction(NoteEditorAction.OnSaveClick)
            awaitItem()
            assertThat(repo.savedNotes).hasSize(1)
            assertThat(repo.savedNotes[0].title).isEqualTo(emojiTitle)
            assertThat(repo.savedNotes[0].content).isEqualTo(emojiContent)
        }
    }

    @Test
    fun `restores title and content from saved state handle after process death`() = runTest(testDispatcher) {
        val handle = SavedStateHandle()
        val repo = FakeNoteRepository()
        val viewModel = NoteEditorViewModel(null, handle, repo)

        viewModel.onAction(NoteEditorAction.OnTitleChange("Draft title"))
        viewModel.onAction(NoteEditorAction.OnContentChange("Draft content"))

        // Simulate process death: new ViewModel with same handle
        val restoredViewModel = NoteEditorViewModel(null, handle, FakeNoteRepository())

        assertThat(restoredViewModel.state.value.title).isEqualTo("Draft title")
        assertThat(restoredViewModel.state.value.content).isEqualTo("Draft content")
    }
}