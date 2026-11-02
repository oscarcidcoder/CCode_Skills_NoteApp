package org.cidcode.notetake.feature.notes.domain

import assertk.assertThat
import assertk.assertions.*
import org.cidcode.notetake.core.domain.Result
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class NoteValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = ["", "   ", "\n", "\t \n "])
    fun `blank titles are rejected`(title: String) {
        assertThat(NoteValidator.validate(title))
            .isEqualTo(Result.Error(NoteValidationError.EMPTY_TITLE))
    }

    @Test
    fun `a title of exactly the maximum code points is accepted`() {
        val title = "\uD83D\uDE00".repeat(NoteValidator.MAX_TITLE_LENGTH)

        assertThat(NoteValidator.validate(title)).isEqualTo(Result.Success(Unit))
    }

    @Test
    fun `a title one code point over the maximum is rejected`() {
        val title = "\uD83D\uDE00".repeat(NoteValidator.MAX_TITLE_LENGTH + 1)

        assertThat(NoteValidator.validate(title))
            .isEqualTo(Result.Error(NoteValidationError.TITLE_TOO_LONG))
    }

    @Test
    fun `zero width joiner sequences count as their code points, not UTF-16 units`() {
        // Family emoji: 11 UTF-16 units, 7 code points. 18 of them are 126 code points.
        val family = "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66"
        val title = family.repeat(18)

        assertThat(NoteValidator.validate(title))
            .isEqualTo(Result.Error(NoteValidationError.TITLE_TOO_LONG))
    }

    @Test
    fun `surrounding whitespace does not count toward the limit`() {
        val title = "  " + "\uD83D\uDE00".repeat(NoteValidator.MAX_TITLE_LENGTH) + "  "

        assertThat(NoteValidator.validate(title)).isEqualTo(Result.Success(Unit))
    }

    @Test
    fun `normalizing trims surrounding whitespace but keeps inner spacing`() {
        assertThat(NoteValidator.normalizeTitle("  shopping   list \n"))
            .isEqualTo("shopping   list")
    }
}
