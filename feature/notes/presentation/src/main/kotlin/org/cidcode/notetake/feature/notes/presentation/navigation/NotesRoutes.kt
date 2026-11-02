package org.cidcode.notetake.feature.notes.presentation.navigation

import kotlinx.serialization.Serializable
import org.cidcode.notetake.feature.notes.domain.NEW_NOTE_ID

@Serializable
data object NoteListRoute

@Serializable
data class NoteEditorRoute(val noteId: Long = NEW_NOTE_ID)
