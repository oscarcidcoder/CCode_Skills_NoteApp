package org.cidcode.notetake.feature.notes.domain

import org.cidcode.notetake.core.domain.Error

enum class NoteValidationError : Error {
    EMPTY_TITLE,
    TITLE_TOO_LONG
}
