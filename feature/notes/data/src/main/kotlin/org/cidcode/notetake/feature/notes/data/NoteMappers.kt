package org.cidcode.notetake.feature.notes.data

import org.cidcode.notetake.feature.notes.domain.Note

fun NoteEntity.toNote(): Note = Note(
    id = id,
    title = title,
    body = body,
    updatedAt = updatedAt
)

fun Note.toNoteEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    body = body,
    updatedAt = updatedAt
)
