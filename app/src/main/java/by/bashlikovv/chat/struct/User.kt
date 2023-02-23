package by.bashlikovv.chat.struct

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val userId: Long = 0,
    val userName: String = "",
    @DrawableRes val userImage: Int = 0
) : Parcelable