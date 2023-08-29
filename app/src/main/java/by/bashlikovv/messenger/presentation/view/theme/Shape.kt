package by.bashlikovv.messenger.presentation.view.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
)

/**
 * @param alignment true equals alignment to the end, false equals alignment to the start
 * @param radius radius of rounding
 * @param iHeight pointer height
 * @param iWidth pointer width
 * @param iEndPadding indent between the edge of the pointer and the shape
 * */
class MessageShape(
    private val alignment: Boolean,
    private val radius: Dp = 25.dp,
    private val iHeight: Dp = 10.dp,
    private val iWidth: Dp = 10.dp,
    private val iEndPadding: Dp = 25.dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            if (alignment) {
                endAlignedPath(size)
            } else {
                startAlignedPath(size)
            }
        }
        return Outline.Generic(path)
    }

    private fun Path.endAlignedPath(size: Size) {
        /*Top-left*/
        moveTo(0f, radius.value)
        cubicTo(
            0f, radius.value,
            0f, 0f,
            radius.value, 0f
        )
        lineTo(size.width - iEndPadding.value - radius.value, 0f)
        /*Top-right*/
        cubicTo(
            size.width - iEndPadding.value - radius.value, 0f,
            size.width - iEndPadding.value, 0f,
            size.width - iEndPadding.value, radius.value
        )
        lineTo(size.width - iEndPadding.value, size.height - iEndPadding.value)
        /*Bottom-right*/
        cubicTo(
            size.width - iEndPadding.value, size.height - iHeight.value,
            size.width + iWidth.value, size.height,
            size.width - iEndPadding.value, size.height
        )
        lineTo(radius.value, size.height)
        /*Bottom-left*/
        cubicTo(
            radius.value, size.height,
            0f, size.height,
            0f, size.height - radius.value
        )
    }

    private fun Path.startAlignedPath(size: Size) {
        /*Top-left*/
        moveTo(iEndPadding.value, radius.value)
        lineTo(iEndPadding.value, size.height - iEndPadding.value)
        /*Bottom-left*/
        cubicTo(
            iEndPadding.value, size.height - iHeight.value,
            -iWidth.value, size.height,
            iEndPadding.value, size.height
        )
        lineTo(size.width - radius.value, size.height)
        /*Bottom-right*/
        cubicTo(
            size.width - radius.value, size.height,
            size.width, size.height,
            size.width, size.height - radius.value
        )
        lineTo(size.width, radius.value)
        /*Top-right*/
        cubicTo(
            size.width, radius.value,
            size.width, 0f,
            size.width - radius.value, 0f
        )
        lineTo(iEndPadding.value + radius.value, 0f)
        /*Top-left*/
        cubicTo(
            iEndPadding.value + radius.value, 0f,
            iEndPadding.value, 0f,
            iEndPadding.value, radius.value
        )
    }
}