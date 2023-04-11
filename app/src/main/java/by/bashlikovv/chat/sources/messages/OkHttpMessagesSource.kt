package by.bashlikovv.chat.sources.messages

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import by.bashlikovv.chat.Const
import by.bashlikovv.chat.app.utils.SecurityUtilsImpl
import by.bashlikovv.chat.sources.base.BaseOkHttpSource
import by.bashlikovv.chat.sources.base.OkHttpConfig
import by.bashlikovv.chat.sources.messages.entities.*
import by.bashlikovv.chat.sources.structs.Message
import okhttp3.MultipartBody
import okhttp3.Request
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer

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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getImage(uri: String): Bitmap {
        var image: Bitmap? = null
        val socket = Socket()
        val input: DataInputStream
        try {
            socket.connect(Const.BASE_URL)

            val request = "GET /get-image?$uri\r\nContent-Type:image/jpg \r\n\r\n"

            socket.getOutputStream().write(request.encodeToByteArray())
            socket.getOutputStream().flush()
            input = DataInputStream(socket.getInputStream())

            val sizeAr = ByteArray(4, init = { 0 })
            input.read(sizeAr)
            val size = ByteBuffer.wrap(sizeAr).asIntBuffer().get()

            val imageAr = ByteArray(size)
            input.readFully(imageAr)

            image = BitmapFactory.decodeByteArray(imageAr, 0, size)
        } catch (_: Exception) {
        } finally {
            socket.close()
        }

        return image ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }

    private fun Socket.connect(baseUrl: String) {
        val ip = baseUrl.substringAfter("://").substringBefore(":")
        val port = baseUrl.substringAfter("$ip:").toInt()
        this.connect(InetSocketAddress(ip, port))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun sendImage(image: Bitmap, room: String, owner: String) {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val addImageRequestBody = AddImageRequestBody(
            image = stream.toByteArray(),
            room = SecurityUtilsImpl().stringToBytes(room),
            owner = SecurityUtilsImpl().stringToBytes(owner)
        )
        val body = MultipartBody.Builder()
            .addPart(addImageRequestBody.toJsonRequestBody())
            .build()
        val request = Request.Builder()
            .post(body)
            .endpoint("/add-image")
            .build()
        client.newCall(request).suspendEnqueue()
    }
}