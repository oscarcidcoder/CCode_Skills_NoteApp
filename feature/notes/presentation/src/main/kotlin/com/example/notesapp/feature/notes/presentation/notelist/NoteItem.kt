package com.example.notesapp.feature.notes.presentation.notelist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notesapp.core.designsystem.components.AppCard
import com.example.notesapp.core.designsystem.icons.AppIcons
import com.example.notesapp.core.designsystem.theme.AppTheme
import com.example.notesapp.feature.notes.presentation.NoteUi
import com.example.notesapp.core.designsystem.R as DesignSystemR

@Composable
fun NoteItem(
    title: String,
    preview: String,
    formattedDate: String,
    onNoteClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier,
        onClick = onNoteClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (preview.isNotEmpty()) {
                    Text(
                        text = preview,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    painter = AppIcons.Delete,
                    contentDescription = stringResource(DesignSystemR.string.cd_delete_note),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(name = "Note Item - Default")
@Preview(name = "Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NoteItemPreview() {
    AppTheme {
        NoteItem(
            title = "Meeting notes",
            preview = "Discussed the quarterly roadmap and next steps...",
            formattedDate = "Mar 15, 2026",
            onNoteClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(name = "Note Item - Long title & content", widthDp = 320, fontScale = 2f)
@Composable
private fun NoteItemLongTextPreview() {
    AppTheme {
        NoteItem(
            title = "Quarterly planning sync with the whole product and design organization across multiple teams",
            preview = "This is a very long note content that should wrap or truncate properly across multiple lines without breaking the layout or causing any visual glitches in the UI.",
            formattedDate = "Mar 15, 2026",
            onNoteClick = {},
            onDeleteClick = {}
        )
    }
}