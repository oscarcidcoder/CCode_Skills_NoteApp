package com.example.notesapp.feature.notes.presentation.noteeditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notesapp.core.designsystem.components.AppButton
import com.example.notesapp.core.designsystem.components.AppTextField
import com.example.notesapp.core.designsystem.components.AppTopAppBar
import com.example.notesapp.core.designsystem.icons.AppIcons
import com.example.notesapp.core.designsystem.theme.AppTheme
import com.example.notesapp.core.presentation.ObserveAsEvents
import com.example.notesapp.core.presentation.getString
import com.example.notesapp.core.designsystem.R as DesignSystemR
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun NoteEditorRoot(
    noteId: String?,
    onNavigateBack: () -> Unit,
    viewModel: NoteEditorViewModel = koinViewModel(
        parameters = { parametersOf(noteId) }
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            NoteEditorEvent.OnSaveSuccess -> onNavigateBack()
            NoteEditorEvent.OnDeleteSuccess -> onNavigateBack()
            is NoteEditorEvent.ShowSnackbar -> {
                // Snackbar handled via state error, but this can be used for transient messages
            }
        }
    }

    NoteEditorScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun NoteEditorScreen(
    state: NoteEditorState,
    onAction: (NoteEditorAction) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

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
            AppTopAppBar(
                title = stringResource(
                    if (state.isNewNote) R.string.new_note else R.string.edit_note
                ),
                navigationIcon = AppIcons.ChevronLeft,
                navigationIconContentDescription = stringResource(DesignSystemR.string.cd_back),
                onNavigationClick = onNavigateBack,
                actions = {
                    if (!state.isNewNote) {
                        IconButton(onClick = { onAction(NoteEditorAction.OnDeleteClick) }) {
                            Icon(
                                painter = AppIcons.Delete,
                                contentDescription = stringResource(DesignSystemR.string.cd_delete_note),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            AppTextField(
                value = state.title,
                onValueChange = { onAction(NoteEditorAction.OnTitleChange(it)) },
                label = stringResource(R.string.title_label),
                placeholder = stringResource(R.string.title_placeholder),
                isError = state.error != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = state.content,
                onValueChange = { onAction(NoteEditorAction.OnContentChange(it)) },
                label = stringResource(R.string.content_label),
                placeholder = stringResource(R.string.content_placeholder),
                singleLine = false,
                minLines = 8,
                maxLines = 20,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppButton(
                text = stringResource(R.string.save_note),
                onClick = { onAction(NoteEditorAction.OnSaveClick) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            )

            if (state.isSaving) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.width(20.dp).height(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.saving),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(name = "Note Editor - New")
@Composable
private fun NoteEditorNewPreview() {
    AppTheme {
        NoteEditorScreen(
            state = NoteEditorState(isNewNote = true),
            onAction = {},
            onNavigateBack = {}
        )
    }
}

@Preview(name = "Note Editor - Edit", fontScale = 2f)
@Composable
private fun NoteEditorEditPreview() {
    AppTheme {
        NoteEditorScreen(
            state = NoteEditorState(
                title = "Meeting notes",
                content = "Discussed the quarterly roadmap and next steps for the team.",
                isNewNote = false
            ),
            onAction = {},
            onNavigateBack = {}
        )
    }
}

@Preview(name = "Note Editor - Error")
@Composable
private fun NoteEditorErrorPreview() {
    AppTheme {
        NoteEditorScreen(
            state = NoteEditorState(
                title = "",
                content = "",
                error = com.example.notesapp.core.domain.UiText.StringResource(R.string.error_title_empty),
                isNewNote = true
            ),
            onAction = {},
            onNavigateBack = {}
        )
    }
}