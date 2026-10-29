package com.example.notesapp.feature.notes.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.notesapp.feature.notes.presentation.notelist.NoteListRoot
import com.example.notesapp.feature.notes.presentation.noteeditor.NoteEditorRoot

fun NavGraphBuilder.notesGraph(
    navController: NavHostController,
    onNavigateToEditor: (String?) -> Unit
) {
    composable<NoteListRoute> {
        NoteListRoot(
            onNavigateToDetail = { noteId ->
                onNavigateToEditor(noteId)
            },
            onNavigateToEditor = { noteId ->
                onNavigateToEditor(noteId)
            }
        )
    }
    composable<NoteEditorRoute> { backStackEntry ->
        val route: NoteEditorRoute = backStackEntry.toRoute()
        NoteEditorRoot(
            noteId = route.noteId,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}