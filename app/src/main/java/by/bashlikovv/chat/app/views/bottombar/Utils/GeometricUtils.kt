package by.bashlikovv.chat.app.views.bottombar.Utils

fun lerp(start: Float, stop: Float, fraction: Float) =
    (start * (1 - fraction) + stop * fraction)