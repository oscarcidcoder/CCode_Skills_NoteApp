package org.cidcode.notetake.feature.notes.presentation.editor

sealed interface NoteEditorAction {
    data class OnNoteRequested(val noteId: Long) : NoteEditorAction
    data class OnTitleChange(val title: String) : NoteEditorAction
    data class OnBodyChange(val body: String) : NoteEditorAction
    data object OnSaveClick : NoteEditorAction
}
