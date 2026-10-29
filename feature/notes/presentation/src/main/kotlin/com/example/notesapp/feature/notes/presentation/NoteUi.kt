package com.example.notesapp.feature.notes.presentation

import com.example.notesapp.feature.notes.domain.Note
import com.example.notesapp.feature.notes.domain.NoteError
import com.example.notesapp.core.domain.UiText

data class NoteUi(
    val id: String,
    val title: String,
    val preview: String,
    val formattedDate: String
)

fun Note.toNoteUi(): NoteUi {
    val preview = if (content.length > 100) content.take(100) + "…" else content
    return NoteUi(
        id = id,
        title = title,
        preview = preview,
        formattedDate = formatDate(updatedAt.toEpochMilliseconds())
    )
}

private fun formatDate(epochMillis: Long): String {
    val instant = java.time.Instant.ofEpochMilli(epochMillis)
    val date = java.time.ZonedDateTime.ofInstant(instant, java.time.ZoneId.systemDefault())
    return date.format(java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy"))
}

fun NoteError.toUiText(): UiText {
    return when (this) {
        NoteError.NOT_FOUND -> UiText.StringResource(R.string.error_note_not_found)
        NoteError.VALIDATION_TITLE_EMPTY -> UiText.StringResource(R.string.error_title_empty)
        NoteError.VALIDATION_CONTENT_TOO_LONG -> UiText.StringResource(R.string.error_content_too_long)
        NoteError.UNKNOWN -> UiText.StringResource(com.example.notesapp.core.presentation.R.string.error_unknown)
    }
}