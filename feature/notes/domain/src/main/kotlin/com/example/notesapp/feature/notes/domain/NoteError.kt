package com.example.notesapp.feature.notes.domain

import com.example.notesapp.core.domain.Error

enum class NoteError : Error {
    NOT_FOUND,
    VALIDATION_TITLE_EMPTY,
    VALIDATION_CONTENT_TOO_LONG,
    UNKNOWN
}