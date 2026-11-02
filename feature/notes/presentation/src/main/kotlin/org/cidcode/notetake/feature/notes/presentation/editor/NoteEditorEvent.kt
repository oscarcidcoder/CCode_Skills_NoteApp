package org.cidcode.notetake.feature.notes.presentation.editor

sealed interface NoteEditorEvent {
    data object NoteSaved : NoteEditorEvent
}
