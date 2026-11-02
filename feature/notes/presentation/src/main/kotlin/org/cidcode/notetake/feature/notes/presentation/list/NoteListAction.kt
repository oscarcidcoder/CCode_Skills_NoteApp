package org.cidcode.notetake.feature.notes.presentation.list

sealed interface NoteListAction {
    data object OnRetryClick : NoteListAction
    data class OnDeleteRequested(val noteId: Long) : NoteListAction
    data object OnDeleteConfirmed : NoteListAction
    data object OnDeleteDismissed : NoteListAction
}
