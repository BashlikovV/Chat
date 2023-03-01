package by.bashlikovv.chat.struct

import android.os.Parcelable
import by.bashlikovv.chat.model.UserImage
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val userId: Long = 0,
    val userName: String = "",
    val userEmail: String = "",
    val userPassword: String = "",
    val userImage: UserImage = UserImage()
) : Parcelable