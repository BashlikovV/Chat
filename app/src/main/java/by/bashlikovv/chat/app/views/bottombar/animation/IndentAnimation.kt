package by.bashlikovv.chat.app.views.bottombar.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape

interface IndentAnimation {

    @Composable
    fun animateIndentShapeAsState(
        targetOffset: Offset,
        shapeCornerRadius: ShapeCornerRadius
    ): State<Shape>
}

data class ShapeCornerRadius(
    val topLeft: Float,
    val topRight: Float,
    val bottomRight: Float,
    val bottomLeft: Float,
)

fun shapeCornerRadius(cornerRadius: Float) =
    ShapeCornerRadius(
        topLeft = cornerRadius,
        topRight = cornerRadius,
        bottomRight = cornerRadius,
        bottomLeft = cornerRadius
    )