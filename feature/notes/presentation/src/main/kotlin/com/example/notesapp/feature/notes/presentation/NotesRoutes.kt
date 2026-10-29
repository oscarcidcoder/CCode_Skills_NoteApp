package com.example.notesapp.feature.notes.presentation

import kotlinx.serialization.Serializable

@Serializable
object NoteListRoute

@Serializable
data class NoteEditorRoute(val noteId: String? = null)