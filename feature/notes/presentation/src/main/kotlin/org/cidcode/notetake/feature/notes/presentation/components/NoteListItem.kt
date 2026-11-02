package org.cidcode.notetake.feature.notes.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.cidcode.notetake.core.designsystem.icons.AppIcons
import org.cidcode.notetake.core.designsystem.theme.NoteTakeTheme
import org.cidcode.notetake.feature.notes.presentation.R
import org.cidcode.notetake.feature.notes.presentation.mapping.NoteUi

@Composable
internal fun NoteListItem(
    note: NoteUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                painter = AppIcons.Note,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (note.preview.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = note.preview,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = note.formattedUpdatedAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SwipeToDeleteNoteItem(
    note: NoteUi,
    onNoteClick: () -> Unit,
    onDeleteRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value -> value == SwipeToDismissBoxValue.EndToStart }
    )
    val deleteLabel = stringResource(R.string.cd_delete_note)

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDeleteRequested()
            dismissState.reset()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        modifier = modifier.semantics {
            customActions = listOf(
                CustomAccessibilityAction(label = deleteLabel) {
                    onDeleteRequested()
                    true
                }
            )
        },
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = AppIcons.Delete,
                    contentDescription = stringResource(R.string.cd_delete_note),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    ) {
        NoteListItem(
            note = note,
            onClick = onNoteClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private val sampleNote = NoteUi(
    id = 1L,
    title = "Weekly planning",
    preview = "Review open items, plan the next sprint and write down the decisions.",
    formattedUpdatedAt = "Mar 15, 2026 \u00B7 09:30"
)

@Preview(name = "Note item", showBackground = true)
@Preview(
    name = "Note item dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(name = "Note item long title", showBackground = true, widthDp = 320)
@Composable
private fun NoteListItemPreview() {
    NoteTakeTheme(dynamicColor = false) {
        NoteListItem(
            note = sampleNote.copy(
                title = "Quarterly planning sync with the whole product and design organization"
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Note item without body", showBackground = true)
@Composable
private fun NoteListItemNoBodyPreview() {
    NoteTakeTheme(dynamicColor = false) {
        NoteListItem(
            note = sampleNote.copy(preview = ""),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Swipe to delete", showBackground = true)
@Composable
private fun SwipeToDeleteNoteItemPreview() {
    NoteTakeTheme(dynamicColor = false) {
        SwipeToDeleteNoteItem(
            note = sampleNote,
            onNoteClick = {},
            onDeleteRequested = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
