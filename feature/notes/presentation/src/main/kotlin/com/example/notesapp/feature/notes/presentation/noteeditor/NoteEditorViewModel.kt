package com.example.notesapp.feature.notes.presentation.noteeditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.core.domain.onFailure
import com.example.notesapp.core.domain.onSuccess
import com.example.notesapp.feature.notes.domain.Note
import com.example.notesapp.feature.notes.domain.NoteRepository
import com.example.notesapp.feature.notes.presentation.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.UUID

class NoteEditorViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val noteId: String? = savedStateHandle.get<String>("noteId")

    private val _state = MutableStateFlow(
        NoteEditorState(
            title = savedStateHandle["title"] ?: "",
            content = savedStateHandle["content"] ?: "",
            isNewNote = noteId == null
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<NoteEditorEvent>()
    val events = _events.receiveAsFlow()

    init {
        noteId?.let { loadNote(it) }
    }

    fun onAction(action: NoteEditorAction) {
        when (action) {
            is NoteEditorAction.OnTitleChange -> onTitleChange(action.title)
            is NoteEditorAction.OnContentChange -> onContentChange(action.content)
            is NoteEditorAction.OnSaveClick -> saveNote()
            is NoteEditorAction.OnDeleteClick -> deleteNote(action.noteId)
        }
    }

    private fun onTitleChange(title: String) {
        savedStateHandle["title"] = title
        _state.update { it.copy(title = title, error = null) }
    }

    private fun onContentChange(content: String) {
        savedStateHandle["content"] = content
        _state.update { it.copy(content = content, error = null) }
    }

    private fun loadNote(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            noteRepository.getNote(id)
                .onSuccess { note ->
                    savedStateHandle["title"] = note.title
                    savedStateHandle["content"] = note.content
                    _state.update {
                        it.copy(
                            title = note.title,
                            content = note.content,
                            isSaving = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isSaving = false, error = error.toUiText()) }
                }
        }
    }

    private fun saveNote() {
        val current = _state.value
        if (current.title.isBlank()) {
            _state.update { it.copy(error = com.example.notesapp.core.domain.UiText.StringResource(com.example.notesapp.R.string.error_title_empty)) }
            return
        }

        if (current.content.length > MAX_CONTENT_LENGTH) {
            _state.update { it.copy(error = com.example.notesapp.core.domain.UiText.StringResource(com.example.notesapp.R.string.error_content_too_long)) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val now = Clock.System.now()
            val note = Note(
                id = noteId ?: UUID.randomUUID().toString(),
                title = current.title.trim(),
                content = current.content.trim(),
                createdAt = if (noteId == null) now else getExistingCreatedAt(),
                updatedAt = now
            )
            noteRepository.saveNote(note)
                .onSuccess {
                    _state.update { it.copy(isSaving = false) }
                    _events.send(NoteEditorEvent.OnSaveSuccess)
                }
                .onFailure { error ->
                    _state.update { it.copy(isSaving = false) }
                    _events.send(NoteEditorEvent.ShowSnackbar(error.toUiText()))
                }
        }
    }

    private suspend fun getExistingCreatedAt(): kotlinx.datetime.Instant {
        // For existing notes, we need the original createdAt.
        // Fetch it from repository.
        return noteId?.let { id ->
            noteRepository.getNote(id).let { result ->
                when (result) {
                    is com.example.notesapp.core.domain.Result.Success -> result.data.createdAt
                    is com.example.notesapp.core.domain.Result.Error -> Clock.System.now()
                }
            }
        } ?: Clock.System.now()
    }

    private fun deleteNote(id: String) {
        viewModelScope.launch {
            noteRepository.deleteNote(id)
                .onSuccess {
                    _events.send(NoteEditorEvent.OnDeleteSuccess)
                }
                .onFailure { error ->
                    _events.send(NoteEditorEvent.ShowSnackbar(error.toUiText()))
                }
        }
    }

    companion object {
        const val MAX_CONTENT_LENGTH = 50000
    }
}