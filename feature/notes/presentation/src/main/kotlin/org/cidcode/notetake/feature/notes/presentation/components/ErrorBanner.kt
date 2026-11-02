package org.cidcode.notetake.feature.notes.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.cidcode.notetake.core.designsystem.theme.NoteTakeTheme
import org.cidcode.notetake.feature.notes.presentation.R

@Composable
internal fun ErrorBanner(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium
            )
            if (onRetry != null) {
                TextButton(onClick = onRetry) {
                    Text(
                        text = stringResource(R.string.retry),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Preview(name = "Error banner", showBackground = true)
@Composable
private fun ErrorBannerPreview() {
    NoteTakeTheme(dynamicColor = false) {
        ErrorBanner(
            message = "Something went wrong. Please try again.",
            onRetry = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Error banner without retry", showBackground = true)
@Composable
private fun ErrorBannerWithoutRetryPreview() {
    NoteTakeTheme(dynamicColor = false) {
        ErrorBanner(
            message = "This note no longer exists.",
            modifier = Modifier.padding(16.dp)
        )
    }
}
