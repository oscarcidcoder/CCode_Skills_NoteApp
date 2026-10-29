package com.example.notesapp.feature.notes.presentation

import com.example.notesapp.feature.notes.presentation.notelist.NoteListViewModel
import com.example.notesapp.feature.notes.presentation.noteeditor.NoteEditorViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val notesPresentationModule = module {
    viewModelOf(::NoteListViewModel)
    viewModel { (params: org.koin.core.parameter.ParametersHolder) ->
        val noteId = params.get<String?>("noteId")
        NoteEditorViewModel(
            savedStateHandle = androidx.lifecycle.SavedStateHandle(
                mapOf("noteId" to noteId)
            ),
            noteRepository = get()
        )
    }
}