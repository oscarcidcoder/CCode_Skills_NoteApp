package org.cidcode.notetake.feature.notes.presentation.editor

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.cidcode.notetake.core.designsystem.icons.AppIcons
import org.cidcode.notetake.core.designsystem.theme.NoteTakeTheme
import org.cidcode.notetake.core.presentation.ObserveAsEvents
import org.cidcode.notetake.core.presentation.asString
import org.cidcode.notetake.core.presentation.UiText
import org.cidcode.notetake.feature.notes.presentation.R
import org.cidcode.notetake.feature.notes.presentation.components.ErrorBanner

@Composable
fun NoteEditorRoot(
    noteId: Long,
    onNavigateBack: () -> Unit,
    showBackButton: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: NoteEditorViewModel = noteEditorViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(events = viewModel.events) { event ->
        when (event) {
            NoteEditorEvent.NoteSaved -> onNavigateBack()
        }
    }

    LaunchedEffect(noteId) {
        viewModel.onAction(NoteEditorAction.OnNoteRequested(noteId))
    }

    NoteEditorScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack,
        showBackButton = showBackButton,
        modifier = modifier
    )
}

/**
 * The editor draft is scoped to the host activity rather than to a navigation entry, so
 * switching between the compact and two-pane layouts keeps unsaved input alive.
 */
@Composable
private fun noteEditorViewModel(): NoteEditorViewModel {
    val activity = LocalContext.current as? ComponentActivity
    return if (activity != null) {
        hiltViewModel<NoteEditorViewModel>(viewModelStoreOwner = activity)
    } else {
        hiltViewModel()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    state: NoteEditorState,
    onAction: (NoteEditorAction) -> Unit,
    onNavigateBack: () -> Unit,
    showBackButton: Boolean,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            if (state.isNewNote) R.string.new_note_title else R.string.edit_note
                        )
                    )
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                painter = AppIcons.ArrowBack,
                                contentDescription = stringResource(R.string.cd_navigate_back)
                            )
                        }
                    }
                },
                actions = {
                    TextButton(
                        onClick = { onAction(NoteEditorAction.OnSaveClick) },
                        enabled = !state.isSaving && !state.isLoading
                    ) {
                        Text(text = stringResource(R.string.save_note))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .imePadding()
        ) {
            if (state.isSaving) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
            }

            state.error?.let { error ->
                ErrorBanner(
                    message = error.asString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                OutlinedTextField(
                    value = state.title,
                    onValueChange = { onAction(NoteEditorAction.OnTitleChange(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(R.string.note_title_label)) },
                    singleLine = true,
                    isError = state.titleError != null
                )
                state.titleError?.let { titleError ->
                    Text(
                        text = titleError.asString(),
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.body,
                    onValueChange = { onAction(NoteEditorAction.OnBodyChange(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    label = { Text(text = stringResource(R.string.note_body_label)) }
                )
            }
        }
    }
}

@Preview(name = "New note", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun NoteEditorScreenNewPreview() {
    NoteTakeTheme(dynamicColor = false) {
        NoteEditorScreen(
            state = NoteEditorState(title = "Meeting notes"),
            onAction = {},
            onNavigateBack = {},
            showBackButton = true
        )
    }
}

@Preview(name = "Edit note", showBackground = true, widthDp = 600, heightDp = 720)
@Composable
private fun NoteEditorScreenEditPreview() {
    NoteTakeTheme(dynamicColor = false) {
        NoteEditorScreen(
            state = NoteEditorState(
                noteId = 7L,
                title = "Weekly planning",
                body = "Review open items and plan the next sprint."
            ),
            onAction = {},
            onNavigateBack = {},
            showBackButton = false
        )
    }
}

@Preview(name = "Title error", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun NoteEditorScreenErrorPreview() {
    NoteTakeTheme(dynamicColor = false) {
        NoteEditorScreen(
            state = NoteEditorState(
                titleError = UiText.StringResource(R.string.error_empty_title)
            ),
            onAction = {},
            onNavigateBack = {},
            showBackButton = true
        )
    }
}

@Preview(name = "Loading note", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun NoteEditorScreenLoadingPreview() {
    NoteTakeTheme(dynamicColor = false) {
        NoteEditorScreen(
            state = NoteEditorState(noteId = 7L, isLoading = true),
            onAction = {},
            onNavigateBack = {},
            showBackButton = true
        )
    }
}
