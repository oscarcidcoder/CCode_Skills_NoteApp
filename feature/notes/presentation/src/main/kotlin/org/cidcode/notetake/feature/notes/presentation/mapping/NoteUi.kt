package org.cidcode.notetake.feature.notes.presentation.mapping

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import org.cidcode.notetake.feature.notes.domain.Note

data class NoteUi(
    val id: Long,
    val title: String,
    val preview: String,
    val formattedUpdatedAt: String
)

private const val PREVIEW_MAX_CODE_POINTS = 120
private const val DATE_PATTERN = "MMM d, yyyy \u00B7 HH:mm"
private val whitespaceRegex = Regex("\\s+")

fun Note.toNoteUi(
    zoneId: ZoneId = ZoneId.systemDefault(),
    locale: Locale = Locale.getDefault()
): NoteUi = NoteUi(
    id = id,
    title = title,
    preview = body.toPreview(),
    formattedUpdatedAt = DateTimeFormatter.ofPattern(DATE_PATTERN, locale)
        .withZone(zoneId)
        .format(Instant.ofEpochMilli(updatedAt))
)

internal fun String.toPreview(maxCodePoints: Int = PREVIEW_MAX_CODE_POINTS): String {
    val collapsed = trim().replace(whitespaceRegex, " ")
    if (collapsed.isEmpty()) return ""

    if (collapsed.codePointCount(0, collapsed.length) <= maxCodePoints) return collapsed

    val end = collapsed.offsetByCodePoints(0, maxCodePoints)
    return collapsed.substring(0, end).trimEnd() + "\u2026"
}
