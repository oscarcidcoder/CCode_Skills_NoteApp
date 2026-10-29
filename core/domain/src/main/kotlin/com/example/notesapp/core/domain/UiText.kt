package com.example.notesapp.core.domain

import androidx.annotation.StringRes

sealed interface UiText {
    data class DynamicString(val value: String) : UiText
    class StringResource(@StringRes val id: Int, val args: Array<Any> = emptyArray()) : UiText
}