package com.example.notesapp.feature.notes.presentation.noteeditor

import com.example.notesapp.core.domain.UiText

sealed interface NoteEditorEvent {
    data object OnSaveSuccess : NoteEditorEvent
    data object OnDeleteSuccess : NoteEditorEvent
    data class ShowSnackbar(val message: UiText) : NoteEditorEvent
}