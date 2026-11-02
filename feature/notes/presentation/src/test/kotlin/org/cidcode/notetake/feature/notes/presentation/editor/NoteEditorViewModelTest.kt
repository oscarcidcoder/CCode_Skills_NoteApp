package org.cidcode.notetake.feature.notes.presentation.editor

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.*
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.cidcode.notetake.core.domain.DataError
import org.cidcode.notetake.core.domain.Result
import org.cidcode.notetake.core.presentation.UiText
import org.cidcode.notetake.feature.notes.domain.NEW_NOTE_ID
import org.cidcode.notetake.feature.notes.domain.Note
import org.cidcode.notetake.feature.notes.domain.NoteValidator
import org.cidcode.notetake.feature.notes.presentation.FakeNoteLocalDataSource
import org.cidcode.notetake.core.presentation.R as CoreR
import org.cidcode.notetake.feature.notes.presentation.R
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteEditorViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val clock = Clock.fixed(Instant.parse("2026-03-15T09:30:00Z"), ZoneOffset.UTC)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `requesting a new note leaves the editor empty`() = runTest {
        val viewModel = editorViewModel()

        viewModel.onAction(NoteEditorAction.OnNoteRequested(NEW_NOTE_ID))

        assertThat(viewModel.state.value.noteId).isEqualTo(NEW_NOTE_ID)
        assertThat(viewModel.state.value.title).isEmpty()
        assertThat(viewModel.state.value.body).isEmpty()
        assertThat(viewModel.state.value.isNewNote).isTrue()
    }

    @Test
    fun `requesting an existing note loads its content`() = runTest {
        val source = FakeNoteLocalDataSource()
        source.notes.value = listOf(Note(id = 7L, title = "Existing", body = "Body", updatedAt = 1L))
        val viewModel = editorViewModel(source)

        viewModel.onAction(NoteEditorAction.OnNoteRequested(7L))
        val state = viewModel.await { !it.isLoading }

        assertThat(state.title).isEqualTo("Existing")
        assertThat(state.body).isEqualTo("Body")
        assertThat(state.noteId).isEqualTo(7L)
        assertThat(state.isNewNote).isFalse()
    }

    @Test
    fun `requesting a note that no longer exists reports it and falls back to a new note`() = runTest {
        val source = FakeNoteLocalDataSource()
        source.getNoteResult = Result.Success(null)
        val viewModel = editorViewModel(source)

        viewModel.onAction(NoteEditorAction.OnNoteRequested(7L))
        val state = viewModel.await { !it.isLoading }

        assertThat(state.noteId).isEqualTo(NEW_NOTE_ID)
        assertThat((state.error as UiText.StringResource).id)
            .isEqualTo(R.string.error_note_not_found)
    }

    @Test
    fun `a failed load surfaces the data error and stops loading`() = runTest {
        val source = FakeNoteLocalDataSource()
        source.getNoteResult = Result.Error(DataError.Local.DISK_FULL)
        val viewModel = editorViewModel(source)

        viewModel.onAction(NoteEditorAction.OnNoteRequested(7L))
        val state = viewModel.await { !it.isLoading }

        assertThat(state.isLoading).isFalse()
        assertThat((state.error as UiText.StringResource).id).isEqualTo(CoreR.string.error_disk_full)
    }

    @Test
    fun `a blank title is rejected without touching the data source`() = runTest {
        val source = FakeNoteLocalDataSource()
        val viewModel = editorViewModel(source)
        viewModel.onAction(NoteEditorAction.OnNoteRequested(NEW_NOTE_ID))
        viewModel.onAction(NoteEditorAction.OnTitleChange("   "))

        viewModel.onAction(NoteEditorAction.OnSaveClick)

        assertThat(source.upsertedNotes).isEmpty()
        assertThat((viewModel.state.value.titleError as UiText.StringResource).id)
            .isEqualTo(R.string.error_empty_title)
    }

    @Test
    fun `a title beyond the code point limit is rejected without touching the data source`() = runTest {
        val source = FakeNoteLocalDataSource()
        val viewModel = editorViewModel(source)
        viewModel.onAction(NoteEditorAction.OnNoteRequested(NEW_NOTE_ID))
        viewModel.onAction(
            NoteEditorAction.OnTitleChange("\uD83D\uDE00".repeat(NoteValidator.MAX_TITLE_LENGTH + 1))
        )

        viewModel.onAction(NoteEditorAction.OnSaveClick)

        assertThat(source.upsertedNotes).isEmpty()
        assertThat((viewModel.state.value.titleError as UiText.StringResource).id)
            .isEqualTo(R.string.error_title_too_long)
    }

    @Test
    fun `a title of exactly the code point limit is saved unchanged`() = runTest {
        val source = FakeNoteLocalDataSource()
        val viewModel = editorViewModel(source)
        val title = "\uD83D\uDE00".repeat(NoteValidator.MAX_TITLE_LENGTH)
        viewModel.onAction(NoteEditorAction.OnNoteRequested(NEW_NOTE_ID))
        viewModel.onAction(NoteEditorAction.OnTitleChange(title))

        viewModel.onAction(NoteEditorAction.OnSaveClick)

        assertThat(source.upsertedNotes).hasSize(1)
        assertThat(source.upsertedNotes.single().title).isEqualTo(title)
    }

    @Test
    fun `editing the title clears a previous validation error`() = runTest {
        val viewModel = editorViewModel()
        viewModel.onAction(NoteEditorAction.OnNoteRequested(NEW_NOTE_ID))
        viewModel.onAction(NoteEditorAction.OnSaveClick)
        assertThat(viewModel.state.value.titleError).isNotNull()

        viewModel.onAction(NoteEditorAction.OnTitleChange("Now valid"))

        assertThat(viewModel.state.value.titleError).isNull()
    }

    @Test
    fun `saving trims the title and stamps the update time from the clock`() = runTest {
        val source = FakeNoteLocalDataSource()
        val viewModel = editorViewModel(source)
        viewModel.onAction(NoteEditorAction.OnNoteRequested(NEW_NOTE_ID))
        viewModel.onAction(NoteEditorAction.OnTitleChange("  Shopping list  "))
        viewModel.onAction(NoteEditorAction.OnBodyChange("Milk, eggs \uD83E\uDD5A"))

        viewModel.onAction(NoteEditorAction.OnSaveClick)

        val saved = source.upsertedNotes.single()
        assertThat(saved.title).isEqualTo("Shopping list")
        assertThat(saved.body).isEqualTo("Milk, eggs \uD83E\uDD5A")
        assertThat(saved.updatedAt).isEqualTo(clock.millis())
        assertThat(viewModel.state.value.isSaving).isFalse()
    }

    @Test
    fun `a saved new note does not leak into the next new note`() = runTest {
        val source = FakeNoteLocalDataSource()
        val viewModel = editorViewModel(source)
        viewModel.onAction(NoteEditorAction.OnNoteRequested(NEW_NOTE_ID))
        viewModel.onAction(NoteEditorAction.OnTitleChange("First note"))
        viewModel.onAction(NoteEditorAction.OnBodyChange("First body"))
        viewModel.onAction(NoteEditorAction.OnSaveClick)

        viewModel.onAction(NoteEditorAction.OnNoteRequested(NEW_NOTE_ID))

        assertThat(viewModel.state.value.title).isEmpty()
        assertThat(viewModel.state.value.body).isEmpty()
        assertThat(viewModel.state.value.noteId).isEqualTo(NEW_NOTE_ID)
    }

    @Test
    fun `a successful save emits NoteSaved`() = runTest {
        val viewModel = editorViewModel()
        viewModel.onAction(NoteEditorAction.OnNoteRequested(NEW_NOTE_ID))
        viewModel.onAction(NoteEditorAction.OnTitleChange("Title"))

        viewModel.events.test {
            viewModel.onAction(NoteEditorAction.OnSaveClick)

            assertThat(awaitItem()).isEqualTo(NoteEditorEvent.NoteSaved)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a failed save surfaces the error and stops saving`() = runTest {
        val source = FakeNoteLocalDataSource()
        source.upsertError = DataError.Local.DISK_FULL
        val viewModel = editorViewModel(source)
        viewModel.onAction(NoteEditorAction.OnNoteRequested(NEW_NOTE_ID))
        viewModel.onAction(NoteEditorAction.OnTitleChange("Title"))

        viewModel.onAction(NoteEditorAction.OnSaveClick)
        val state = viewModel.await { it.error != null }

        assertThat(state.isSaving).isFalse()
        assertThat((state.error as UiText.StringResource).id).isEqualTo(CoreR.string.error_disk_full)
        assertThat(source.upsertedNotes).isEmpty()
    }

    @Test
    fun `a second save while the first is in flight is ignored`() = runTest {
        val source = FakeNoteLocalDataSource()
        val gate = CompletableDeferred<Unit>()
        source.upsertGate = gate
        val viewModel = editorViewModel(source)
        viewModel.onAction(NoteEditorAction.OnNoteRequested(NEW_NOTE_ID))
        viewModel.onAction(NoteEditorAction.OnTitleChange("Title"))

        viewModel.onAction(NoteEditorAction.OnSaveClick)
        viewModel.onAction(NoteEditorAction.OnSaveClick)
        gate.complete(Unit)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(source.upsertedNotes).hasSize(1)
        assertThat(viewModel.state.value.isSaving).isFalse()
    }

    @Test
    fun `requesting the same note again preserves the in-progress draft`() = runTest {
        val source = FakeNoteLocalDataSource()
        source.notes.value = listOf(Note(id = 7L, title = "Original", body = "", updatedAt = 1L))
        val viewModel = editorViewModel(source)
        viewModel.onAction(NoteEditorAction.OnNoteRequested(7L))
        viewModel.await { !it.isLoading }
        viewModel.onAction(NoteEditorAction.OnTitleChange("Edited"))

        viewModel.onAction(NoteEditorAction.OnNoteRequested(7L))

        assertThat(viewModel.state.value.title).isEqualTo("Edited")
        assertThat(source.getNoteCalls).isEqualTo(1)
    }

    @Test
    fun `a draft restored after process death is not reloaded from the data source`() = runTest {
        val source = FakeNoteLocalDataSource()
        val savedState = SavedStateHandle(
            mapOf(
                "noteId" to 7L,
                "title" to "Draft title",
                "body" to "Draft body",
                "initialized" to true
            )
        )
        val viewModel = NoteEditorViewModel(savedState, source, clock)

        viewModel.onAction(NoteEditorAction.OnNoteRequested(7L))

        assertThat(viewModel.state.value.title).isEqualTo("Draft title")
        assertThat(viewModel.state.value.body).isEqualTo("Draft body")
        assertThat(source.getNoteCalls).isEqualTo(0)
    }

    @Test
    fun `a new note draft restored after process death is preserved`() = runTest {
        val source = FakeNoteLocalDataSource()
        val savedState = SavedStateHandle(
            mapOf(
                "noteId" to NEW_NOTE_ID,
                "title" to "Unsaved draft",
                "body" to "Typed but not saved",
                "initialized" to true
            )
        )
        val viewModel = NoteEditorViewModel(savedState, source, clock)

        viewModel.onAction(NoteEditorAction.OnNoteRequested(NEW_NOTE_ID))

        assertThat(viewModel.state.value.title).isEqualTo("Unsaved draft")
        assertThat(viewModel.state.value.body).isEqualTo("Typed but not saved")
        assertThat(viewModel.state.value.isNewNote).isTrue()
    }

    @Test
    fun `title and body edits are written to SavedStateHandle`() = runTest {
        val savedState = SavedStateHandle()
        val viewModel = NoteEditorViewModel(savedState, FakeNoteLocalDataSource(), clock)

        viewModel.onAction(NoteEditorAction.OnTitleChange("Title"))
        viewModel.onAction(NoteEditorAction.OnBodyChange("Body"))

        assertThat(savedState.get<String>("title")).isEqualTo("Title")
        assertThat(savedState.get<String>("body")).isEqualTo("Body")
    }

    private fun editorViewModel(
        source: FakeNoteLocalDataSource = FakeNoteLocalDataSource()
    ): NoteEditorViewModel = NoteEditorViewModel(SavedStateHandle(), source, clock)

    private suspend fun NoteEditorViewModel.await(
        predicate: (NoteEditorState) -> Boolean
    ): NoteEditorState = state.first(predicate)
}
