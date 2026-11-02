package org.cidcode.notetake.feature.notes.domain

import org.cidcode.notetake.core.domain.EmptyResult
import org.cidcode.notetake.core.domain.Result

object NoteValidator {

    const val MAX_TITLE_LENGTH = 120

    fun validate(title: String): EmptyResult<NoteValidationError> {
        val normalized = normalizeTitle(title)
        return when {
            normalized.isEmpty() -> Result.Error(NoteValidationError.EMPTY_TITLE)
            normalized.codePointCount(0, normalized.length) > MAX_TITLE_LENGTH ->
                Result.Error(NoteValidationError.TITLE_TOO_LONG)

            else -> Result.Success(Unit)
        }
    }

    /**
     * Trims surrounding whitespace. Length rules count Unicode code points, so an
     * emoji or a ZWJ sequence is one character rather than several UTF-16 units.
     */
    fun normalizeTitle(title: String): String = title.trim()
}
