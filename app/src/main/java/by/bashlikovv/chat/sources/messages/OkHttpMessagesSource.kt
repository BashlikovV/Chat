package by.bashlikovv.chat.sources.messages

import android.os.Build
import androidx.annotation.RequiresApi
import by.bashlikovv.chat.app.utils.SecurityUtilsImpl
import by.bashlikovv.chat.sources.base.BaseOkHttpSource
import by.bashlikovv.chat.sources.base.OkHttpConfig
import by.bashlikovv.chat.sources.messages.entities.AddMessageRequestBody
import by.bashlikovv.chat.sources.messages.entities.AddMessageResponseBody
import by.bashlikovv.chat.sources.messages.entities.RoomMessagesRequestBody
import by.bashlikovv.chat.sources.messages.entities.RoomMessagesResponseBody
import okhttp3.Request

class OkHttpMessagesSource(
    config: OkHttpConfig
) : BaseOkHttpSource(config) {

    suspend fun getRoomMessages(room: String): List<by.bashlikovv.chat.sources.structs.Message> {
        val getRoomMessagesRequestBody = RoomMessagesRequestBody(
            room = room
        )
        val request = Request.Builder()
            .post(getRoomMessagesRequestBody.toJsonRequestBody())
            .endpoint("/room-messages")
            .build()
        val response = client.newCall(request).suspendEnqueue()
        return response.parseJsonResponse<RoomMessagesResponseBody>().messages
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun sendMessage(message: by.bashlikovv.chat.sources.structs.Message, me: String): String {
        val securityUtilsImpl = SecurityUtilsImpl()
        val addMessagesRequestBody = AddMessageRequestBody(
            image = message.image,
            file = message.file.toString(),
            value = message.value,
            time = message.time,
            owner = securityUtilsImpl.bytesToString(message.room.user1.token),
            receiver = securityUtilsImpl.bytesToString(message.room.user2.token),
            from = me
        )
        val request = Request.Builder()
            .post(addMessagesRequestBody.toJsonRequestBody())
            .endpoint("/add-message")
            .build()
        val response = client.newCall(request).suspendEnqueue()
        return response.parseJsonResponse<AddMessageResponseBody>().result
    }
}