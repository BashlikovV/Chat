package by.bashlikovv.chat.navbar

import androidx.annotation.DrawableRes
import by.bashlikovv.chat.R

data class TopNavBarState(
    @DrawableRes val leadingIcon: Int = R.drawable.menu,
    val description: String = "menu"
)
