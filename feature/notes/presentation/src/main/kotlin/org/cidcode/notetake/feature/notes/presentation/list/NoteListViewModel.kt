package org.cidcode.notetake.feature.notes.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.cidcode.notetake.core.domain.Result
import org.cidcode.notetake.core.domain.onFailure
import org.cidcode.notetake.core.domain.onSuccess
import org.cidcode.notetake.core.presentation.toUiText
import org.cidcode.notetake.feature.notes.domain.NoteLocalDataSource
import org.cidcode.notetake.feature.notes.presentation.mapping.toNoteUi

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val noteLocalDataSource: NoteLocalDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(NoteListState())
    val state = _state.asStateFlow()

    private var observeJob: Job? = null

    init {
        observeNotes()
    }

    fun onAction(action: NoteListAction) {
        when (action) {
            NoteListAction.OnRetryClick -> observeNotes()

            is NoteListAction.OnDeleteRequested -> _state.update { current ->
                current.copy(
                    notePendingDeletion = current.notes.firstOrNull { it.id == action.noteId }
                )
            }

            NoteListAction.OnDeleteConfirmed -> deletePendingNote()
            NoteListAction.OnDeleteDismissed -> _state.update { it.copy(notePendingDeletion = null) }
        }
    }

    private fun observeNotes() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            noteLocalDataSource.observeNotes().collect { result ->
                when (result) {
                    is Result.Success -> _state.update {
                        it.copy(
                            notes = result.data.map { note -> note.toNoteUi() },
                            isLoading = false,
                            error = null
                        )
                    }

                    is Result.Error -> _state.update {
                        it.copy(isLoading = false, error = result.error.toUiText())
                    }
                }
            }
        }
    }

    private fun deletePendingNote() {
        val pending = _state.value.notePendingDeletion ?: return
        viewModelScope.launch {
            noteLocalDataSource.deleteNote(pending.id)
                .onSuccess { _state.update { it.copy(notePendingDeletion = null) } }
                .onFailure { error ->
                    _state.update {
                        it.copy(notePendingDeletion = null, error = error.toUiText())
                    }
                }
        }
    }
}
