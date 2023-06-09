package by.bashlikovv.chat.app.views.bottombar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.Placeable

@Composable
fun animatedNavBarMeasurePolicy(
    onBallPositionsCalculated: (ArrayList<Float>) -> Unit
) = remember {
    barMeasurePolicy(onBallPositionsCalculated = onBallPositionsCalculated)
}

internal fun barMeasurePolicy(onBallPositionsCalculated: (ArrayList<Float>) -> Unit) =
    MeasurePolicy { measures, constraints ->

        check(measures.isNotEmpty()){
            "There must be at least one element"
        }
        val itemWidth = constraints.maxWidth / measures.size
        val placeable = measures.map { measurable ->
            measurable.measure(constraints.copy(maxWidth = itemWidth))
        }

        val gap = calculateGap(placeable, constraints.maxWidth)
        val height = placeable.maxOf { it.height }

        layout(constraints.maxWidth, height) {
            var xPosition = gap

            val positions = arrayListOf<Float>()

            placeable.forEachIndexed { index, _ ->
                placeable[index].placeRelative(xPosition, 0)

                positions.add(
                    element = calculatePointPosition(
                        xPosition,
                        placeable[index].width,
                    )
                )

                xPosition += placeable[index].width + gap
            }
            onBallPositionsCalculated(positions)
        }
    }

fun calculatePointPosition(xButtonPosition: Int, buttonWidth: Int): Float {
    return xButtonPosition + (buttonWidth / 2f)
}

fun calculateGap(placeable: List<Placeable>, width: Int): Int {
    var allWidth = 0
    placeable.forEach { item ->
        allWidth += item.width
    }
    return (width - allWidth) / (placeable.size + 1)
}