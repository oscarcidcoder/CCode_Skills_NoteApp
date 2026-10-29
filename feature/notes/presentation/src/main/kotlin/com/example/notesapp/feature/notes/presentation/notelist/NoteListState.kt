package com.example.notesapp.feature.notes.presentation.notelist

import com.example.notesapp.core.domain.UiText

data class NoteListState(
    val notes: List<com.example.notesapp.feature.notes.presentation.NoteUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null
)