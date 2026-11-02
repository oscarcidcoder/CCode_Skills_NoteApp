package org.cidcode.notetake.feature.notes.presentation.editor

import org.cidcode.notetake.core.presentation.UiText
import org.cidcode.notetake.feature.notes.domain.NEW_NOTE_ID

data class NoteEditorState(
    val noteId: Long = NEW_NOTE_ID,
    val title: String = "",
    val body: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val titleError: UiText? = null,
    val error: UiText? = null
) {
    val isNewNote: Boolean
        get() = noteId == NEW_NOTE_ID
}
