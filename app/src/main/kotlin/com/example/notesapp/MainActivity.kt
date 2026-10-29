package com.example.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.notesapp.core.designsystem.theme.AppTheme
import com.example.notesapp.feature.notes.presentation.NoteEditorRoute
import com.example.notesapp.feature.notes.presentation.NoteListRoute
import com.example.notesapp.feature.notes.presentation.notesGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                NotesNavHost()
            }
        }
    }
}

@Composable
fun NotesNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NoteListRoute
    ) {
        notesGraph(
            navController = navController,
            onNavigateToEditor = { noteId ->
                navController.navigate(NoteEditorRoute(noteId))
            }
        )
    }
}