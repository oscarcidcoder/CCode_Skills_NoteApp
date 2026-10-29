package com.example.notesapp.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.collect

@Composable
fun <E> ObserveAsEvents(
    events: Flow<E>,
    onEvent: (E) -> Unit
) {
    LaunchedEffect(Unit) {
        events.collect { event ->
            onEvent(event)
        }
    }
}

@Composable
fun <E> ObserveAsEvents(
    events: Channel<E>,
    onEvent: (E) -> Unit
) {
    val flow = remember(events) { events.consumeAsFlow() }
    LaunchedEffect(flow) {
        flow.collect { event ->
            onEvent(event)
        }
    }
}