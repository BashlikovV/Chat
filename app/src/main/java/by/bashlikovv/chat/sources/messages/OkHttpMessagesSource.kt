package by.bashlikovv.chat.sources.messages

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import by.bashlikovv.chat.Const
import by.bashlikovv.chat.app.utils.SecurityUtilsImpl
import by.bashlikovv.chat.sources.HttpContract
import by.bashlikovv.chat.sources.base.BaseOkHttpSource
import by.bashlikovv.chat.sources.base.OkHttpConfig
import by.bashlikovv.chat.sources.messages.entities.*
import by.bashlikovv.chat.sources.structs.ServerMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    suspend fun getRoomMessages(room: String, pagination: IntRange): GetServerMessagesResult {
        return try {
            val getRoomMessagesRequestBody = RoomMessagesRequestBody(
                room = room,
                pagination = pagination
            )
            val request = Request.Builder()
                .post(getRoomMessagesRequestBody.toJsonRequestBody())
                .endpoint("/${HttpContract.UrlMethods.ROOM_MESSAGES}")
                .build()
            val response = client.newCall(request).suspendEnqueue()
            val result = response.parseJsonResponse<RoomMessagesResponseBody>()
            GetServerMessagesResult(
                serverMessages = result.messages,
                unreadMessagesCount = result.unreadMessagesCount
            )
        } catch (e: Exception) {
            e.printStackTrace()
            GetServerMessagesResult()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun sendMessage(serverMessage: ServerMessage, me: String): String {
        return try {
            val securityUtilsImpl = SecurityUtilsImpl()
            val addMessagesRequestBody = AddMessageRequestBody(
                image = serverMessage.image,
                file = serverMessage.file,
                value = serverMessage.value.decodeToString(),
                time = serverMessage.time,
                owner = securityUtilsImpl.bytesToString(serverMessage.room.user1.token),
                receiver = securityUtilsImpl.bytesToString(serverMessage.room.user2.token),
                from = me
            )
            val request = Request.Builder()
                .post(addMessagesRequestBody.toJsonRequestBody())
                .endpoint("/${HttpContract.UrlMethods.ADD_MESSAGE}")
                .build()
            val response = client.newCall(request).suspendEnqueue()
            return response.parseJsonResponse<AddMessageResponseBody>().result
        } catch (e: Exception) {
            e.printStackTrace()
            "ERROR"
        }
    }

    suspend fun deleteMessage(vararg serverMessages: ServerMessage): String {
        return try {
            val deleteMessageRequestBody = DeleteMessageRequestBody(
                messages = serverMessages.toList()
            )
            val request = Request.Builder()
                .post(deleteMessageRequestBody.toJsonRequestBody())
                .endpoint("/${HttpContract.UrlMethods.DELETE_MESSAGE}")
                .build()
            val response = client.newCall(request).suspendEnqueue()
            response.message
        } catch (e: Exception) {
            e.printStackTrace()
            "ERROR"
        }
    }

    private fun Socket.connect() {
        val ip = Const.BASE_URL.substringAfter("://").substringBefore(":")
        val port = Const.BASE_URL.substringAfter("$ip:").toInt()
        try {
            this.connect(InetSocketAddress(ip, port), 1000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getImage(uri: String): Bitmap {
        var image: Bitmap? = null
        val socket = Socket()
        val input: DataInputStream
        try {
            socket.connect()

            val request = "GET /${HttpContract.UrlMethods.GET_IMAGE}?$uri\r\nContent-Type:image/jpg \r\n\r\n"

            withContext(Dispatchers.IO) {
                socket.getOutputStream().write(request.encodeToByteArray())
                socket.getOutputStream().flush()
                input = DataInputStream(socket.getInputStream())

                val sizeAr = ByteArray(4, init = { 0 })
                input.read(sizeAr)
                val size = ByteBuffer.wrap(sizeAr).asIntBuffer().get()

                val imageAr = ByteArray(size)
                input.readFully(imageAr)

                image = BitmapFactory.decodeByteArray(imageAr, 0, size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            withContext(Dispatchers.IO) {
                socket.close()
            }
        }

        return image ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun sendImage(
        image: Bitmap,
        room: String,
        owner: String,
        isSignUp: Boolean
    ): String {
        try {
            val stream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 50, stream)

            val roomBytes = if (isSignUp) {
                room.encodeToByteArray()
            } else {
                SecurityUtilsImpl().stringToBytes(room)
            }
            val ownerBytes = if (isSignUp) {
                owner.encodeToByteArray()
            } else {
                SecurityUtilsImpl().stringToBytes(owner)
            }

            val addImageRequestBody = AddImageRequestBody(
                image = stream.toByteArray(),
                room = roomBytes,
                owner = ownerBytes
            )
            val body = MultipartBody.Builder()
                .addPart(addImageRequestBody.toJsonRequestBody())
                .build()
            val request = Request.Builder()
                .post(body)
                .endpoint("/${HttpContract.UrlMethods.ADD_IMAGE}")
                .build()
            val response = client.newCall(request).suspendEnqueue()
            return response.parseJsonResponse<AddImageResponseBody>().imageUri.decodeToString()
        } catch (e: Exception) {
            e.printStackTrace()
            return e.message.toString()
        }
    }

    suspend fun readRoomMessages(room: String) {
        try {
            val readMessagesRequestBody = ReadMessagesRequestBody(room)
            val request = Request.Builder()
                .post(readMessagesRequestBody.toJsonRequestBody())
                .endpoint("/${HttpContract.UrlMethods.READ_MESSAGES}")
                .build()
            client.newCall(request).suspendEnqueue()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}