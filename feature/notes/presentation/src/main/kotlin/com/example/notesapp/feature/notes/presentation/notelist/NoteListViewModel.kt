package com.example.notesapp.feature.notes.presentation.notelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.core.domain.onFailure
import com.example.notesapp.core.domain.onSuccess
import com.example.notesapp.feature.notes.domain.NoteRepository
import com.example.notesapp.feature.notes.presentation.toNoteUi
import com.example.notesapp.feature.notes.presentation.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteListViewModel(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NoteListState())
    val state = _state.asStateFlow()

    init {
        observeNotes()
    }

    private fun observeNotes() {
        viewModelScope.launch {
            noteRepository.getNotes().collect { result ->
                result
                    .onSuccess { notes ->
                        _state.update {
                            it.copy(
                                notes = notes.map { note -> note.toNoteUi() },
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(isLoading = false, error = error.toUiText())
                        }
                    }
            }
        }
    }

    fun onAction(action: NoteListAction) {
        when (action) {
            is NoteListAction.OnRefreshClick -> refreshNotes()
            is NoteListAction.OnDeleteNote -> deleteNote(action.noteId)
        }
    }

    private fun refreshNotes() {
        // The Flow collection already handles loading. Set loading state for UX.
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // Re-collect is not needed; the Flow will emit. Briefly show spinner.
            kotlinx.coroutines.delay(300)
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun deleteNote(noteId: String) {
        viewModelScope.launch {
            noteRepository.deleteNote(noteId)
                .onFailure { error ->
                    _state.update { it.copy(error = error.toUiText()) }
                }
        }
    }
}