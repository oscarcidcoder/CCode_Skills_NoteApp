package org.cidcode.notetake.feature.notes.presentation.list

import org.cidcode.notetake.core.presentation.UiText
import org.cidcode.notetake.feature.notes.presentation.mapping.NoteUi

data class NoteListState(
    val notes: List<NoteUi> = emptyList(),
    val isLoading: Boolean = true,
    val error: UiText? = null,
    val notePendingDeletion: NoteUi? = null
) {
    val showEmptyState: Boolean
        get() = !isLoading && notes.isEmpty()
}
