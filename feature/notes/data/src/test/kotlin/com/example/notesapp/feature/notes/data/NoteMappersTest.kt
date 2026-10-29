package com.example.notesapp.feature.notes.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.example.notesapp.feature.notes.domain.Note
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Test

class NoteMappersTest {

    @Test
    fun `note to entity round trip preserves all fields`() {
        val instant = Instant.fromEpochMilliseconds(1_700_000_000_123L)
        val note = Note(
            id = "abc-123",
            title = "👨‍👩‍👧‍👦 Family note",
            content = "Combining e\u0301 + CJK 你好",
            createdAt = instant,
            updatedAt = instant
        )

        val entity = note.toNoteEntity()
        val roundTripped = entity.toNote()

        assertThat(roundTripped.id).isEqualTo(note.id)
        assertThat(roundTripped.title).isEqualTo(note.title)
        assertThat(roundTripped.content).isEqualTo(note.content)
        assertThat(roundTripped.createdAt).isEqualTo(note.createdAt)
        assertThat(roundTripped.updatedAt).isEqualTo(note.updatedAt)
    }

    @Test
    fun `entity to note converts epoch millis to instant`() {
        val entity = NoteEntity(
            id = "1",
            title = "T",
            content = "C",
            createdAt = 1_700_000_000_000L,
            updatedAt = 1_700_000_000_500L
        )

        val note = entity.toNote()

        assertThat(note.createdAt.toEpochMilliseconds()).isEqualTo(1_700_000_000_000L)
        assertThat(note.updatedAt.toEpochMilliseconds()).isEqualTo(1_700_000_000_500L)
    }
}