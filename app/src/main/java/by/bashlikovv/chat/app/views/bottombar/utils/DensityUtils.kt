package by.bashlikovv.chat.app.views.bottombar.utils

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

@Stable
fun Dp.toPxf(density: Density): Float = with(density) { this@toPxf.toPx() }