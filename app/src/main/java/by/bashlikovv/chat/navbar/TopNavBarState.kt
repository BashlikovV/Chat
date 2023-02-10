package by.bashlikovv.chat.navbar

import androidx.annotation.DrawableRes

data class TopNavBarState(
    @DrawableRes val leadingIcon: Int = 0,
    val description: String = ""
)
