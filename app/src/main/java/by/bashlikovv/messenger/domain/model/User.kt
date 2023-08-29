package by.bashlikovv.messenger.domain.model

import android.os.Parcelable
import by.bashlikovv.messenger.presentation.view.login.UserImage
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class User(
    val userId: Long = 0,
    val userName: String = "",
    val userEmail: String = "",
    val userPassword: String = "",
    val userToken: String = "",
    val lastConnectionTime: Date = Date(),
    val userImage: UserImage = UserImage()
) : Parcelable