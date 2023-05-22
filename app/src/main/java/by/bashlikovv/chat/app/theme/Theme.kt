package by.bashlikovv.chat.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val darkColorsPalette = darkColors(
    background = Color(0xFF131313),
    primary = Color(0xFF1E1E1E),
    secondary = Color(0xFF1C6D78),
    onSurface = Color(0xFFFFFFFF),
    surface = Color(0xFF9D9C9C),
    onError = Color(0xFFE39801)
)

val lightColorsPalette = darkColors(
    background = Color(0xFFFFFFFF),
    primary = Color(0xFFF8F8F8),
    secondary = Color(0xFF92D4DD),
    onSurface = Color(0xFF000000),
    surface = Color(0xFF5E5E5E),
    onError = Color(0xFFE39801)
)

@Composable
fun MessengerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        darkColorsPalette
    } else {
        lightColorsPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}