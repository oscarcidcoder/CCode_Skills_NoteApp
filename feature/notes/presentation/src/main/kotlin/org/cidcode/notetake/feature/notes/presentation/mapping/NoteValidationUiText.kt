package org.cidcode.notetake.feature.notes.presentation.mapping

import org.cidcode.notetake.core.presentation.UiText
import org.cidcode.notetake.feature.notes.domain.NoteValidationError
import org.cidcode.notetake.feature.notes.domain.NoteValidator
import org.cidcode.notetake.feature.notes.presentation.R

fun NoteValidationError.toUiText(): UiText = when (this) {
    NoteValidationError.EMPTY_TITLE -> UiText.StringResource(R.string.error_empty_title)
    NoteValidationError.TITLE_TOO_LONG -> UiText.StringResource(
        R.string.error_title_too_long,
        arrayOf(NoteValidator.MAX_TITLE_LENGTH)
    )
}
