package org.cidcode.notetake.feature.notes.presentation.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.cidcode.notetake.core.designsystem.icons.AppIcons
import org.cidcode.notetake.core.designsystem.theme.NoteTakeTheme
import org.cidcode.notetake.core.presentation.UiText
import org.cidcode.notetake.core.presentation.asString
import org.cidcode.notetake.feature.notes.presentation.R
import org.cidcode.notetake.feature.notes.presentation.components.DeleteNoteDialog
import org.cidcode.notetake.feature.notes.presentation.components.EmptyNotes
import org.cidcode.notetake.feature.notes.presentation.components.ErrorBanner
import org.cidcode.notetake.feature.notes.presentation.components.SwipeToDeleteNoteItem
import org.cidcode.notetake.feature.notes.presentation.mapping.NoteUi

@Composable
fun NoteListRoot(
    onNoteClick: (Long) -> Unit,
    onCreateNoteClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    NoteListScreen(
        state = state,
        onAction = viewModel::onAction,
        onNoteClick = onNoteClick,
        onCreateNoteClick = onCreateNoteClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    state: NoteListState,
    onAction: (NoteListAction) -> Unit,
    onNoteClick: (Long) -> Unit,
    onCreateNoteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.notes_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateNoteClick,
                icon = {
                    Icon(
                        painter = AppIcons.Add,
                        contentDescription = null
                    )
                },
                text = { Text(text = stringResource(R.string.new_note)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            state.error?.let { error ->
                ErrorBanner(
                    message = error.asString(),
                    onRetry = { onAction(NoteListAction.OnRetryClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.showEmptyState -> {
                    EmptyNotes(onCreateNoteClick = onCreateNoteClick)
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 96.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = state.notes, key = { note -> note.id }) { note ->
                            SwipeToDeleteNoteItem(
                                note = note,
                                onNoteClick = { onNoteClick(note.id) },
                                onDeleteRequested = {
                                    onAction(NoteListAction.OnDeleteRequested(note.id))
                                }
                            )
                        }
                    }
                }
            }
        }

        state.notePendingDeletion?.let { note ->
            DeleteNoteDialog(
                noteTitle = note.title,
                onConfirm = { onAction(NoteListAction.OnDeleteConfirmed) },
                onDismiss = { onAction(NoteListAction.OnDeleteDismissed) }
            )
        }
    }
}

private val sampleNotes = listOf(
    NoteUi(
        id = 1L,
        title = "Weekly planning",
        preview = "Review open items, plan the next sprint and write down the decisions.",
        formattedUpdatedAt = "Mar 15, 2026 \u00B7 09:30"
    ),
    NoteUi(
        id = 2L,
        title = "Grocery list",
        preview = "Milk, eggs, coffee, olive oil",
        formattedUpdatedAt = "Mar 14, 2026 \u00B7 18:02"
    )
)

@Preview(name = "Note list", showBackground = true, widthDp = 360, heightDp = 720)
@Preview(
    name = "Note list dark",
    showBackground = true,
    widthDp = 360,
    heightDp = 720,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(name = "Note list wide", showBackground = true, widthDp = 840, heightDp = 720)
@Composable
private fun NoteListScreenPreview() {
    NoteTakeTheme(dynamicColor = false) {
        NoteListScreen(
            state = NoteListState(notes = sampleNotes, isLoading = false),
            onAction = {},
            onNoteClick = {},
            onCreateNoteClick = {}
        )
    }
}

@Preview(name = "Note list empty", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun NoteListScreenEmptyPreview() {
    NoteTakeTheme(dynamicColor = false) {
        NoteListScreen(
            state = NoteListState(notes = emptyList(), isLoading = false),
            onAction = {},
            onNoteClick = {},
            onCreateNoteClick = {}
        )
    }
}

@Preview(name = "Note list loading", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun NoteListScreenLoadingPreview() {
    NoteTakeTheme(dynamicColor = false) {
        NoteListScreen(
            state = NoteListState(isLoading = true),
            onAction = {},
            onNoteClick = {},
            onCreateNoteClick = {}
        )
    }
}

@Preview(name = "Note list error", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun NoteListScreenErrorPreview() {
    NoteTakeTheme(dynamicColor = false) {
        NoteListScreen(
            state = NoteListState(
                notes = sampleNotes,
                isLoading = false,
                error = UiText.StringResource(R.string.error_note_not_found)
            ),
            onAction = {},
            onNoteClick = {},
            onCreateNoteClick = {}
        )
    }
}

@Preview(name = "Delete note dialog", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun NoteListScreenDeleteDialogPreview() {
    NoteTakeTheme(dynamicColor = false) {
        NoteListScreen(
            state = NoteListState(
                notes = sampleNotes,
                isLoading = false,
                notePendingDeletion = sampleNotes.first()
            ),
            onAction = {},
            onNoteClick = {},
            onCreateNoteClick = {}
        )
    }
}
