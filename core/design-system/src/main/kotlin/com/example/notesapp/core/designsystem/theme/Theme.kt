package com.example.notesapp.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = BrandColors.lightPrimary,
    onPrimary = BrandColors.lightOnPrimary,
    primaryContainer = BrandColors.lightPrimaryContainer,
    onPrimaryContainer = BrandColors.lightOnPrimaryContainer,
    secondary = BrandColors.lightSecondary,
    onSecondary = BrandColors.lightOnSecondary,
    secondaryContainer = BrandColors.lightSecondaryContainer,
    onSecondaryContainer = BrandColors.lightOnSecondaryContainer,
    surface = BrandColors.lightSurface,
    onSurface = BrandColors.lightOnSurface,
    surfaceVariant = BrandColors.lightSurfaceVariant,
    onSurfaceVariant = BrandColors.lightOnSurfaceVariant,
    error = BrandColors.lightError,
    onError = BrandColors.lightOnError,
    errorContainer = BrandColors.lightErrorContainer,
    onErrorContainer = BrandColors.lightOnErrorContainer,
    outline = BrandColors.lightOutline
)

private val DarkColors = darkColorScheme(
    primary = BrandColors.darkPrimary,
    onPrimary = BrandColors.darkOnPrimary,
    primaryContainer = BrandColors.darkPrimaryContainer,
    onPrimaryContainer = BrandColors.darkOnPrimaryContainer,
    secondary = BrandColors.darkSecondary,
    onSecondary = BrandColors.darkOnSecondary,
    secondaryContainer = BrandColors.darkSecondaryContainer,
    onSecondaryContainer = BrandColors.darkOnSecondaryContainer,
    surface = BrandColors.darkSurface,
    onSurface = BrandColors.darkOnSurface,
    surfaceVariant = BrandColors.darkSurfaceVariant,
    onSurfaceVariant = BrandColors.darkOnSurfaceVariant,
    error = BrandColors.darkError,
    onError = BrandColors.darkOnError,
    errorContainer = BrandColors.darkErrorContainer,
    onErrorContainer = BrandColors.darkOnErrorContainer,
    outline = BrandColors.darkOutline
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}