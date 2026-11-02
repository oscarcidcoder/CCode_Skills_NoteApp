package org.cidcode.notetake.feature.notes.presentation.mapping

import assertk.assertThat
import assertk.assertions.*
import java.time.ZoneId
import java.util.Locale
import org.cidcode.notetake.feature.notes.domain.Note
import org.junit.jupiter.api.Test

class NoteUiTest {

    private val utc = ZoneId.of("UTC")
    private val us = Locale.US

    @Test
    fun `maps note fields and formats the timestamp in the provided zone`() {
        val note = Note(
            id = 3L,
            title = "Meeting notes",
            body = "Decisions",
            updatedAt = 0L
        )

        val ui = note.toNoteUi(zoneId = utc, locale = us)

        assertThat(ui.id).isEqualTo(3L)
        assertThat(ui.title).isEqualTo("Meeting notes")
        assertThat(ui.preview).isEqualTo("Decisions")
        assertThat(ui.formattedUpdatedAt).isEqualTo("Jan 1, 1970 \u00B7 00:00")
    }

    @Test
    fun `formatting respects the zone, not the raw epoch`() {
        val note = Note(id = 1L, title = "T", body = "", updatedAt = 0L)

        val berlin = note.toNoteUi(zoneId = ZoneId.of("Europe/Berlin"), locale = us)

        assertThat(berlin.formattedUpdatedAt).isEqualTo("Jan 1, 1970 \u00B7 01:00")
    }

    @Test
    fun `a blank body produces an empty preview`() {
        val note = Note(id = 1L, title = "T", body = "  \n\t ", updatedAt = 0L)

        assertThat(note.toNoteUi(zoneId = utc, locale = us).preview).isEmpty()
    }

    @Test
    fun `newlines and tabs collapse into single spaces`() {
        val body = "  Line one\n\nLine\ttwo  "

        assertThat(body.toPreview()).isEqualTo("Line one Line two")
    }

    @Test
    fun `a body exactly at the limit is not truncated`() {
        val body = "\uD83D\uDE00".repeat(120)

        assertThat(body.toPreview()).isEqualTo(body)
    }

    @Test
    fun `truncation never splits a surrogate pair`() {
        val body = "\uD83D\uDE00".repeat(130)

        val preview = body.toPreview()

        assertThat(preview).endsWith("\u2026")
        assertThat(preview.dropLast(1)).isEqualTo("\uD83D\uDE00".repeat(120))
    }

    @Test
    fun `truncation appends a single ellipsis`() {
        val body = "A".repeat(300)

        val preview = body.toPreview()

        assertThat(preview).endsWith("\u2026")
        assertThat(preview.length).isEqualTo(121)
    }
}
