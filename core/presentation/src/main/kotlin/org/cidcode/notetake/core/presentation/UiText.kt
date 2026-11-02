package org.cidcode.notetake.core.presentation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource

/**
 * A piece of text that may come from an Android string resource.
 */
sealed interface UiText {
    data class DynamicString(val value: String) : UiText
    class StringResource(
        @StringRes val id: Int,
        val args: Array<Any> = emptyArray()
    ) : UiText
}

@Composable
@ReadOnlyComposable
fun UiText.asString(): String = when (this) {
    is UiText.DynamicString -> value
    is UiText.StringResource -> stringResource(id, *args)
}
