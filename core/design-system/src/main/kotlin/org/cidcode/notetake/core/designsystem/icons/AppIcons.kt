package org.cidcode.notetake.core.designsystem.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import org.cidcode.notetake.core.designsystem.R

/**
 * Single source of truth for icons used across features.
 */
object AppIcons {
    val Add: Painter @Composable get() = painterResource(R.drawable.ic_add)
    val ArrowBack: Painter @Composable get() = painterResource(R.drawable.ic_arrow_back)
    val Delete: Painter @Composable get() = painterResource(R.drawable.ic_delete)
    val Edit: Painter @Composable get() = painterResource(R.drawable.ic_edit)
    val Note: Painter @Composable get() = painterResource(R.drawable.ic_note)
}
