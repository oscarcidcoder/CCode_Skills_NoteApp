package org.cidcode.notetake.feature.notes.domain

const val NEW_NOTE_ID = 0L

data class Note(
    val id: Long = NEW_NOTE_ID,
    val title: String,
    val body: String,
    val updatedAt: Long
)
