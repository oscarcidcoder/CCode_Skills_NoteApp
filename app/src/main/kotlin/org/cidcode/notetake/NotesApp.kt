package org.cidcode.notetake

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.cidcode.notetake.feature.notes.domain.NEW_NOTE_ID
import org.cidcode.notetake.feature.notes.presentation.editor.EmptyEditorPane
import org.cidcode.notetake.feature.notes.presentation.editor.NoteEditorRoot
import org.cidcode.notetake.feature.notes.presentation.list.NoteListRoot
import org.cidcode.notetake.feature.notes.presentation.navigation.NoteEditorRoute
import org.cidcode.notetake.feature.notes.presentation.navigation.NoteListRoute
import org.cidcode.notetake.feature.notes.presentation.navigation.notesGraph

private const val LIST_PANE_WEIGHT = 0.38f
private const val DETAIL_PANE_WEIGHT = 0.62f

@Composable
fun NotesApp(
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    val isTwoPane = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    val navController = rememberNavController()
    var selectedNoteId by rememberSaveable { mutableStateOf<Long?>(null) }
    var wasTwoPane by rememberSaveable { mutableStateOf(isTwoPane) }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val compactEditorNoteId = backStackEntry.toEditorNoteId()

    // Keep the open note alive while the window crosses the compact/expanded breakpoint.
    LaunchedEffect(isTwoPane, compactEditorNoteId) {
        if (isTwoPane) {
            wasTwoPane = true
            if (selectedNoteId == null) {
                selectedNoteId = compactEditorNoteId
            }
        } else if (wasTwoPane) {
            wasTwoPane = false
            val noteId = selectedNoteId
            selectedNoteId = null
            // The compact stack may still hold the editor that was open before the
            // two-pane session. Replace it so back always returns to the list.
            if (compactEditorNoteId != null) {
                navController.popBackStack(NoteListRoute, inclusive = false)
            }
            if (noteId != null) {
                navController.navigate(NoteEditorRoute(noteId = noteId)) {
                    launchSingleTop = true
                }
            }
        }
    }

    if (isTwoPane) {
        TwoPaneNotes(
            selectedNoteId = selectedNoteId,
            onNoteSelected = { selectedNoteId = it },
            onCreateNoteClick = { selectedNoteId = NEW_NOTE_ID },
            onNavigateBack = { selectedNoteId = null },
            modifier = modifier
        )
    } else {
        SinglePaneNotes(
            navController = navController,
            modifier = modifier
        )
    }
}

@Composable
private fun TwoPaneNotes(
    selectedNoteId: Long?,
    onNoteSelected: (Long) -> Unit,
    onCreateNoteClick: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxSize()) {
        NoteListRoot(
            onNoteClick = onNoteSelected,
            onCreateNoteClick = onCreateNoteClick,
            modifier = Modifier.weight(LIST_PANE_WEIGHT)
        )

        VerticalDivider(modifier = Modifier.fillMaxHeight())

        if (selectedNoteId == null) {
            EmptyEditorPane(
                onCreateNoteClick = onCreateNoteClick,
                modifier = Modifier.weight(DETAIL_PANE_WEIGHT)
            )
        } else {
            BackHandler(onBack = onNavigateBack)
            NoteEditorRoot(
                noteId = selectedNoteId,
                onNavigateBack = onNavigateBack,
                showBackButton = false,
                modifier = Modifier.weight(DETAIL_PANE_WEIGHT)
            )
        }
    }
}

@Composable
private fun SinglePaneNotes(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NoteListRoute,
        modifier = modifier.fillMaxSize()
    ) {
        notesGraph(navController = navController)
    }
}

private fun NavBackStackEntry?.toEditorNoteId(): Long? {
    val entry = this ?: return null
    return runCatching { entry.toRoute<NoteEditorRoute>().noteId }.getOrNull()
}
