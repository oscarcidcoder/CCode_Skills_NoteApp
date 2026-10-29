package com.example.notesapp.feature.notes.presentation.noteeditor

sealed interface NoteEditorAction {
    data class OnTitleChange(val title: String) : NoteEditorAction
    data class OnContentChange(val content: String) : NoteEditorAction
    data object OnSaveClick : NoteEditorAction
    data class OnDeleteClick(val noteId: String) : NoteEditorAction
}