package com.example.notesapp.feature.notes.presentation.noteeditor

import com.example.notesapp.core.domain.UiText

data class NoteEditorState(
    val title: String = "",
    val content: String = "",
    val isSaving: Boolean = false,
    val error: UiText? = null,
    val isNewNote: Boolean = true
)