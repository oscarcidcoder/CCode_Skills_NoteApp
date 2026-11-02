package org.cidcode.notetake.feature.notes.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.cidcode.notetake.core.designsystem.icons.AppIcons
import org.cidcode.notetake.core.designsystem.theme.NoteTakeTheme
import org.cidcode.notetake.feature.notes.presentation.R

@Composable
internal fun EmptyNotes(
    onCreateNoteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = AppIcons.Note,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.empty_notes_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.empty_notes_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onCreateNoteClick) {
                Icon(
                    painter = AppIcons.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = stringResource(R.string.create_first_note))
            }
        }
    }
}

@Preview(name = "Empty notes", showBackground = true, widthDp = 360, heightDp = 640)
@Preview(name = "Empty notes wide", showBackground = true, widthDp = 840, heightDp = 640)
@Composable
private fun EmptyNotesPreview() {
    NoteTakeTheme(dynamicColor = false) {
        EmptyNotes(onCreateNoteClick = {})
    }
}
