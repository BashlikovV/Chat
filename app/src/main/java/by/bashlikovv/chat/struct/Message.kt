package by.bashlikovv.chat.struct

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Message(
    val value: String = "",
    val user: User = User(),
    val time: String = "",
    val isRead: Boolean = false
) : Parcelable
