package com.example.notesapp.core.designsystem.icons

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.notesapp.R

object AppIcons {
    val Add: Painter @Composable get() = painterResource(R.drawable.ic_add)
    val Edit: Painter @Composable get() = painterResource(R.drawable.ic_edit)
    val Delete: Painter @Composable get() = painterResource(R.drawable.ic_delete)
    val ChevronLeft: Painter @Composable get() = painterResource(R.drawable.ic_chevron_left)
    val ChevronRight: Painter @Composable get() = painterResource(R.drawable.ic_chevron_right)
    val Note: Painter @Composable get() = painterResource(R.drawable.ic_note)
    val Check: Painter @Composable get() = painterResource(R.drawable.ic_check)
    val Close: Painter @Composable get() = painterResource(R.drawable.ic_close)
    val Search: Painter @Composable get() = painterResource(R.drawable.ic_search)
    val Refresh: Painter @Composable get() = painterResource(R.drawable.ic_refresh)
}