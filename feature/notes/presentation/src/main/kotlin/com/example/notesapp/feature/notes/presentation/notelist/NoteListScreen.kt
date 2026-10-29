package com.example.notesapp.feature.notes.presentation.notelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notesapp.core.designsystem.components.AppEmptyState
import com.example.notesapp.core.designsystem.components.AppTopAppBar
import com.example.notesapp.core.designsystem.icons.AppIcons
import com.example.notesapp.core.designsystem.theme.AppTheme
import com.example.notesapp.core.presentation.UiText
import com.example.notesapp.core.presentation.getString
import com.example.notesapp.feature.notes.presentation.NoteUi
import com.example.notesapp.core.designsystem.R as DesignSystemR
import org.koin.androidx.compose.koinViewModel

@Composable
fun NoteListRoot(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToEditor: (String?) -> Unit,
    viewModel: NoteListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    NoteListScreen(
        state = state,
        onAction = viewModel::onAction,
        onNoteClick = onNavigateToDetail,
        onCreateClick = { onNavigateToEditor(null) },
        onDeleteClick = { viewModel.onAction(NoteListAction.OnDeleteNote(it)) }
    )
}

@Composable
fun NoteListScreen(
    state: NoteListState,
    onAction: (NoteListAction) -> Unit,
    onNoteClick: (String) -> Unit,
    onCreateClick: () -> Unit,
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let { uiText ->
            snackbarHostState.showSnackbar(
                uiText.getString(androidx.compose.ui.platform.LocalContext.current.resources)
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopAppBar(title = stringResource(R.string.notes_title))
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    painter = AppIcons.Add,
                    contentDescription = stringResource(DesignSystemR.string.cd_add_note)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading && state.notes.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.notes.isEmpty() -> {
                    AppEmptyState(
                        icon = AppIcons.Note,
                        title = stringResource(R.string.no_notes_yet),
                        description = stringResource(R.string.tap_add_to_create),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.notes, key = { it.id }) { note ->
                            NoteItem(
                                title = note.title,
                                preview = note.preview,
                                formattedDate = note.formattedDate,
                                onNoteClick = { onNoteClick(note.id) },
                                onDeleteClick = { onDeleteClick(note.id) },
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Note List - Empty")
@Composable
private fun NoteListScreenEmptyPreview() {
    AppTheme {
        NoteListScreen(
            state = NoteListState(),
            onAction = {},
            onNoteClick = {},
            onCreateClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(name = "Note List - With Notes", fontScale = 2f)
@Composable
private fun NoteListScreenWithNotesPreview() {
    AppTheme {
        NoteListScreen(
            state = NoteListState(
                notes = listOf(
                    NoteUi("1", "Meeting notes", "Discussed roadmap", "Mar 15, 2026"),
                    NoteUi("2", "Shopping list", "Milk, eggs, bread", "Mar 14, 2026"),
                    NoteUi("3", "Ideas", "A very long idea that should truncate...", "Mar 13, 2026")
                )
            ),
            onAction = {},
            onNoteClick = {},
            onCreateClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(name = "Note List - Wide", widthDp = 840)
@Composable
private fun NoteListScreenWidePreview() {
    AppTheme {
        NoteListScreen(
            state = NoteListState(
                notes = listOf(
                    NoteUi("1", "Meeting notes", "Discussed roadmap", "Mar 15, 2026"),
                    NoteUi("2", "Shopping list", "Milk, eggs, bread", "Mar 14, 2026")
                )
            ),
            onAction = {},
            onNoteClick = {},
            onCreateClick = {},
            onDeleteClick = {}
        )
    }
}