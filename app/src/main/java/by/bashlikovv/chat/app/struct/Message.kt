package by.bashlikovv.chat.app.struct

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Message(
    val value: String = "",
    val isImage: Boolean = false,
    val imageBitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
    val user: User = User(),
    val time: String = "",
    val isRead: Boolean = false,
    val from: String = ""
) : Parcelable
