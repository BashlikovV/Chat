package by.bashlikovv.chat.sources.messages

import android.os.Build
import androidx.annotation.RequiresApi
import by.bashlikovv.chat.app.utils.SecurityUtilsImpl
import by.bashlikovv.chat.sources.base.BaseOkHttpSource
import by.bashlikovv.chat.sources.base.OkHttpConfig
import by.bashlikovv.chat.sources.messages.entities.*
import by.bashlikovv.chat.sources.structs.Message
import okhttp3.Request

class OkHttpMessagesSource(
    config: OkHttpConfig
) : BaseOkHttpSource(config) {

    suspend fun getRoomMessages(room: String, pagination: IntRange): List<Message> {
        val getRoomMessagesRequestBody = RoomMessagesRequestBody(
            room = room,
            pagination = pagination
        )
        val request = Request.Builder()
            .post(getRoomMessagesRequestBody.toJsonRequestBody())
            .endpoint("/room-messages")
            .build()
        val response = client.newCall(request).suspendEnqueue()
        return response.parseJsonResponse<RoomMessagesResponseBody>().messages
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun sendMessage(message: Message, me: String): String {
        val securityUtilsImpl = SecurityUtilsImpl()
        val addMessagesRequestBody = AddMessageRequestBody(
            image = message.image,
            file = message.file,
            value = message.value.decodeToString(),
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

    suspend fun deleteMessage(message: Message): String {
        val deleteMessageRequestBody = DeleteMessageRequestBody(
            message = message
        )
        val request = Request.Builder()
            .post(deleteMessageRequestBody.toJsonRequestBody())
            .endpoint("/delete-message")
            .build()
        val response = client.newCall(request).suspendEnqueue()
        return response.message
    }
}