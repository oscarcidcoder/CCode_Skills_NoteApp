package com.example.notesapp.feature.notes.presentation.notelist

sealed interface NoteListAction {
    data object OnRefreshClick : NoteListAction
    data class OnDeleteNote(val noteId: String) : NoteListAction
}