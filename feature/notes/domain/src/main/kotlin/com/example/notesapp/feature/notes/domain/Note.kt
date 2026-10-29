package com.example.notesapp.feature.notes.domain

import kotlinx.datetime.Instant

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Instant,
    val updatedAt: Instant
)