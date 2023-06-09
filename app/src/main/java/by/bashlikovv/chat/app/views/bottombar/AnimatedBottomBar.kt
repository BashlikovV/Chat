package by.bashlikovv.chat.app.views.bottombar

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import by.bashlikovv.chat.app.views.bottombar.animation.Height
import by.bashlikovv.chat.app.views.bottombar.animation.IndentAnimation
import by.bashlikovv.chat.app.views.bottombar.animation.ShapeCornerRadius
import by.bashlikovv.chat.app.views.bottombar.animation.shapeCornerRadius

@Composable
fun AnimatedNavigationBar(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    barColor: Color = Color.White,
    cornerRadius: ShapeCornerRadius = shapeCornerRadius(0f),
    indentAnimation: IndentAnimation = Height(tween(300)),
    content: @Composable () -> Unit,
) {

    var itemPositions by remember { mutableStateOf(listOf<Offset>()) }
    val measurePolicy = animatedNavBarMeasurePolicy {
        itemPositions = it.map { xCord ->
            Offset(xCord, 0f)
        }
    }

    val selectedItemOffset by remember(selectedIndex, itemPositions) {
        derivedStateOf {
            if (itemPositions.isNotEmpty()) itemPositions[selectedIndex] else Offset.Unspecified
        }
    }

    val indentShape = indentAnimation.animateIndentShapeAsState(
        shapeCornerRadius = cornerRadius,
        targetOffset = selectedItemOffset
    )

    Box(
        modifier = modifier
    ) {
        Layout(
            modifier = Modifier
                .graphicsLayer {
                    clip = true
                    shape = indentShape.value
                }
                .background(barColor),
            content = content,
            measurePolicy = measurePolicy
        )
    }
}

val ballSize = 10.dp