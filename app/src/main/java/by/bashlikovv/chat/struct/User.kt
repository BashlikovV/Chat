package by.bashlikovv.chat.struct

import androidx.annotation.DrawableRes

data class User(
    val userId: Long = 0,
    val userName: String = "",
    @DrawableRes val userImage: Int = 0
)
