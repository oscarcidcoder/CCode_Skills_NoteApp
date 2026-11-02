package org.cidcode.notetake.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.flow.Flow

/**
 * Collects one-time events from a [Flow] while this composable is in composition.
 */
@Composable
fun <T> ObserveAsEvents(
    events: Flow<T>,
    onEvent: (T) -> Unit
) {
    val currentOnEvent by rememberUpdatedState(onEvent)
    LaunchedEffect(events) {
        events.collect { currentOnEvent(it) }
    }
}
