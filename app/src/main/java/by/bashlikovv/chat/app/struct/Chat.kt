package by.bashlikovv.chat.app.struct

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Chat(
    val user: User = User(),
    val messages: List<Message> = emptyList(),
    val time: String = "",
    val count: Int = 0,
    val token: String = ""
) : Parcelable