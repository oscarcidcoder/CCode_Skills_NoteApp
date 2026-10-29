package com.example.notesapp.core.domain

sealed interface UiText {
    data class DynamicString(val value: String) : UiText
    data class StringResource(val id: Int, val args: List<Any> = emptyList()) : UiText
}