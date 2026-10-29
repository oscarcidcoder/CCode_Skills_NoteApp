package com.example.notesapp.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModelScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

@Composable
fun <E> ObserveAsEvents(
    events: ReceiveChannel<E>,
    onEvent: (E) -> Unit
) {
    LaunchedEffect(events) {
        viewModelScope.launch {
            events.consumeEach { event ->
                onEvent(event)
            }
        }
    }
}