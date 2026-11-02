package org.cidcode.notetake.feature.notes.presentation.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Clock
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.cidcode.notetake.core.domain.Result
import org.cidcode.notetake.core.domain.onFailure
import org.cidcode.notetake.core.domain.onSuccess
import org.cidcode.notetake.core.presentation.UiText
import org.cidcode.notetake.core.presentation.toUiText
import org.cidcode.notetake.feature.notes.domain.NEW_NOTE_ID
import org.cidcode.notetake.feature.notes.domain.Note
import org.cidcode.notetake.feature.notes.domain.NoteLocalDataSource
import org.cidcode.notetake.feature.notes.domain.NoteValidator
import org.cidcode.notetake.feature.notes.presentation.R
import org.cidcode.notetake.feature.notes.presentation.mapping.toUiText

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val noteLocalDataSource: NoteLocalDataSource,
    private val clock: Clock
) : ViewModel() {

    private val _state = MutableStateFlow(
        NoteEditorState(
            noteId = savedStateHandle[KEY_NOTE_ID] ?: NEW_NOTE_ID,
            title = savedStateHandle[KEY_TITLE] ?: "",
            body = savedStateHandle[KEY_BODY] ?: ""
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<NoteEditorEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var isInitialized: Boolean = savedStateHandle[KEY_INITIALIZED] ?: false

    fun onAction(action: NoteEditorAction) {
        when (action) {
            is NoteEditorAction.OnNoteRequested -> openNote(action.noteId)
            is NoteEditorAction.OnTitleChange -> updateTitle(action.title)
            is NoteEditorAction.OnBodyChange -> updateBody(action.body)
            NoteEditorAction.OnSaveClick -> save()
        }
    }

    private fun openNote(noteId: Long) {
        if (isInitialized && _state.value.noteId == noteId) return

        isInitialized = true
        savedStateHandle[KEY_INITIALIZED] = true
        savedStateHandle[KEY_NOTE_ID] = noteId

        if (noteId == NEW_NOTE_ID) {
            savedStateHandle[KEY_TITLE] = ""
            savedStateHandle[KEY_BODY] = ""
            _state.update {
                it.copy(
                    noteId = NEW_NOTE_ID,
                    title = "",
                    body = "",
                    isLoading = false,
                    titleError = null,
                    error = null
                )
            }
            return
        }

        loadNote(noteId)
    }

    private fun loadNote(noteId: Long) {
        viewModelScope.launch {
            _state.update {
                it.copy(noteId = noteId, isLoading = true, titleError = null, error = null)
            }
            noteLocalDataSource.getNote(noteId)
                .onSuccess { note ->
                    if (note == null) {
                        savedStateHandle[KEY_NOTE_ID] = NEW_NOTE_ID
                        savedStateHandle[KEY_TITLE] = ""
                        savedStateHandle[KEY_BODY] = ""
                        _state.update {
                            it.copy(
                                noteId = NEW_NOTE_ID,
                                title = "",
                                body = "",
                                isLoading = false,
                                error = UiText.StringResource(R.string.error_note_not_found)
                            )
                        }
                    } else {
                        savedStateHandle[KEY_TITLE] = note.title
                        savedStateHandle[KEY_BODY] = note.body
                        _state.update {
                            it.copy(
                                noteId = note.id,
                                title = note.title,
                                body = note.body,
                                isLoading = false
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toUiText()) }
                }
        }
    }

    private fun updateTitle(title: String) {
        savedStateHandle[KEY_TITLE] = title
        _state.update { it.copy(title = title, titleError = null) }
    }

    private fun updateBody(body: String) {
        savedStateHandle[KEY_BODY] = body
        _state.update { it.copy(body = body) }
    }

    private fun save() {
        if (_state.value.isSaving) return

        val title = NoteValidator.normalizeTitle(_state.value.title)
        when (val validation = NoteValidator.validate(title)) {
            is Result.Error -> {
                _state.update { it.copy(titleError = validation.error.toUiText()) }
                return
            }

            is Result.Success -> Unit
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            val note = Note(
                id = _state.value.noteId,
                title = title,
                body = _state.value.body,
                updatedAt = clock.millis()
            )
            noteLocalDataSource.upsertNote(note)
                .onSuccess {
                    // The editor closes after a save; a later open must re-read from the
                    // data source instead of reusing this editor's finished state.
                    isInitialized = false
                    savedStateHandle[KEY_INITIALIZED] = false
                    _state.update { it.copy(isSaving = false) }
                    _events.send(NoteEditorEvent.NoteSaved)
                }
                .onFailure { error ->
                    _state.update { it.copy(isSaving = false, error = error.toUiText()) }
                }
        }
    }

    private companion object {
        const val KEY_NOTE_ID = "noteId"
        const val KEY_TITLE = "title"
        const val KEY_BODY = "body"
        const val KEY_INITIALIZED = "initialized"
    }
}
