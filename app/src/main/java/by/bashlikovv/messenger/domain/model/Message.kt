package by.bashlikovv.messenger.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val value: String = "",
    val isImage: Boolean = false,
    val imageBitmap: String = "",
    val user: User = User(),
    val time: String = "",
    val isRead: Boolean = false,
    val from: String = ""
) : Parcelable
