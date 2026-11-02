package org.cidcode.notetake.feature.notes.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.cidcode.notetake.core.designsystem.icons.AppIcons
import org.cidcode.notetake.core.designsystem.theme.NoteTakeTheme
import org.cidcode.notetake.feature.notes.presentation.R

@Composable
internal fun DeleteNoteDialog(
    noteTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                painter = AppIcons.Delete,
                contentDescription = null
            )
        },
        title = { Text(text = stringResource(R.string.delete_note_title)) },
        text = { Text(text = stringResource(R.string.delete_note_message, noteTitle)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.delete),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}

@Preview(name = "Delete note dialog")
@Composable
private fun DeleteNoteDialogPreview() {
    NoteTakeTheme(dynamicColor = false) {
        DeleteNoteDialog(
            noteTitle = "Weekly planning",
            onConfirm = {},
            onDismiss = {}
        )
    }
}
