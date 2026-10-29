package com.example.notesapp.feature.notes.presentation

import androidx.lifecycle.SavedStateHandle
import com.example.notesapp.feature.notes.presentation.notelist.NoteListViewModel
import com.example.notesapp.feature.notes.presentation.noteeditor.NoteEditorViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val notesPresentationModule = module {
    viewModelOf(::NoteListViewModel)

    viewModel { (noteId: String?) ->
        NoteEditorViewModel(
            noteId = noteId,
            savedStateHandle = get(),
            noteRepository = get()
        )
    }
}