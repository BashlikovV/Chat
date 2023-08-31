package by.bashlikovv.messenger.data.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import by.bashlikovv.messenger.data.remote.base.BaseOkHttpSource
import by.bashlikovv.messenger.data.remote.base.OkHttpConfig
import by.bashlikovv.messenger.data.remote.contract.HttpContract
import by.bashlikovv.messenger.data.remote.model.GetServerMessagesResult
import by.bashlikovv.messenger.data.remote.model.ServerMessage
import by.bashlikovv.messenger.data.remote.request.AddImageRequestBody
import by.bashlikovv.messenger.data.remote.request.AddMessageRequestBody
import by.bashlikovv.messenger.data.remote.request.DeleteMessageRequestBody
import by.bashlikovv.messenger.data.remote.request.ReadMessagesRequestBody
import by.bashlikovv.messenger.data.remote.request.RoomMessagesRequestBody
import by.bashlikovv.messenger.data.remote.response.AddImageResponseBody
import by.bashlikovv.messenger.data.remote.response.AddMessageResponseBody
import by.bashlikovv.messenger.data.remote.response.RoomMessagesResponseBody
import by.bashlikovv.messenger.utils.SecurityUtilsImpl
import by.bashlikovv.messenger.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.Request
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer
import java.util.concurrent.TimeoutException

class OkHttpMessagesSource(
    config: OkHttpConfig
) : BaseOkHttpSource(config) {

    companion object {
        const val ARRAY_SIZE = 4
        const val CONNECTION_TIMEOUT = 2500
        const val IMAGE_COMPRESS_QUALITY = 50
    }

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
        } catch (_: TimeoutException) {
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
        } catch (_: Exception) {
            "ERROR"
        }
    }

    suspend fun deleteMessage(serverMessages: List<ServerMessage>): String {
        return try {
            val deleteMessageRequestBody = DeleteMessageRequestBody(
                messages = serverMessages
            )
            val request = Request.Builder()
                .post(deleteMessageRequestBody.toJsonRequestBody())
                .endpoint("/${HttpContract.UrlMethods.DELETE_MESSAGE}")
                .build()
            val response = client.newCall(request).suspendEnqueue()
            response.message
        } catch (_: Exception) {
            "ERROR"
        }
    }

    @Throws(IOException::class)
    private fun Socket.connect() {
        val ip = Constants.BASE_URL.substringAfter("://").substringBefore(":")
        val port = Constants.BASE_URL.substringAfter("$ip:").toInt()
        try {
            connect(InetSocketAddress(ip, port), CONNECTION_TIMEOUT)
        } catch (e: SocketException) {
            e.printStackTrace()
        }
    }

    suspend fun getImage(uri: String): Bitmap {
        var image: Bitmap? = null
        val socket = withContext(Dispatchers.IO) {
            client.socketFactory.createSocket()
        }
        val input: DataInputStream
        try {
            val request = "GET /${HttpContract.UrlMethods.GET_IMAGE}?$uri\r\nContent-Type:image/jpg \r\n\r\n"

            withContext(Dispatchers.IO) {
                socket.connect()
                socket.getOutputStream().write(request.encodeToByteArray())
                input = DataInputStream(socket.getInputStream())

                val sizeAr = ByteArray(ARRAY_SIZE, init = { 0 })
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
            image.compress(Bitmap.CompressFormat.JPEG, IMAGE_COMPRESS_QUALITY, stream)

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
        } catch (_: Exception) {  }
    }
}