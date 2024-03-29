package by.bashlikovv.chat.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = PrimaryDark,
    secondary = SecondaryDark,
    primaryVariant = PrimaryLight,
    background = Background
)

private val LightColorPalette = lightColors(
    primary = SecondaryLight,
    secondary = PrimaryLight,
    primaryVariant = SecondaryLight,
    background = PrimaryLight
)

@Composable
fun MessengerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}