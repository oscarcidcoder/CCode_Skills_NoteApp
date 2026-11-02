package org.cidcode.notetake.feature.notes.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.cidcode.notetake.feature.notes.domain.NEW_NOTE_ID
import org.cidcode.notetake.feature.notes.presentation.editor.NoteEditorRoot
import org.cidcode.notetake.feature.notes.presentation.list.NoteListRoot

fun NavGraphBuilder.notesGraph(navController: NavController) {
    composable<NoteListRoute> {
        NoteListRoot(
            onNoteClick = { noteId ->
                navController.navigate(NoteEditorRoute(noteId = noteId)) {
                    launchSingleTop = true
                }
            },
            onCreateNoteClick = {
                navController.navigate(NoteEditorRoute(noteId = NEW_NOTE_ID)) {
                    launchSingleTop = true
                }
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
