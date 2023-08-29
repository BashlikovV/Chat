package by.bashlikovv.messenger.presentation.views.bottombar.shape

import by.bashlikovv.messenger.presentation.views.bottombar.animation.ShapeCornerRadius
import by.bashlikovv.messenger.presentation.views.bottombar.animation.shapeCornerRadius

data class IndentShapeData(
    val xIndent: Float = 0f,
    val height: Float = 0f,
    val width: Float = 0f,
    val cornerRadius: ShapeCornerRadius = shapeCornerRadius(0f),
    val ballOffset: Float = 0f,
)