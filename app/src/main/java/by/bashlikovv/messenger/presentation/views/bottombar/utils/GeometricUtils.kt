package by.bashlikovv.messenger.presentation.views.bottombar.utils

fun lerp(start: Float, stop: Float, fraction: Float) =
    (start * (1 - fraction) + stop * fraction)