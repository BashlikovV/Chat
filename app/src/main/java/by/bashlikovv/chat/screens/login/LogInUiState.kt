package by.bashlikovv.chat.screens.login

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class LogInUiState(
    val identifier: String = "",
    val password: String = "",
    val userName: String = "",
    val userImageBitmap: UserImage = UserImage(),
    val isIdentifierCorrect: Boolean = true,
    val isPasswordCorrect: Boolean = true,
    val isSuccess: Boolean = false,
    val isHaveAccount: Boolean = false,
    val progressBarVisibility: Boolean = false
)

@Parcelize
data class UserImage(
    val userImageBitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
    val userImageUri: Uri = Uri.EMPTY,
    val userImageUrl: String = ""
) : Parcelable
