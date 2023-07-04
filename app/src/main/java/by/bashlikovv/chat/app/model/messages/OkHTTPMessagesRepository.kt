package by.bashlikovv.chat.app.model.messages

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import by.bashlikovv.chat.Repositories
import by.bashlikovv.chat.app.model.messages.entities.GetMessagesResult
import by.bashlikovv.chat.app.screens.chat.ChatUiState
import by.bashlikovv.chat.app.screens.login.UserImage
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.struct.Message
import by.bashlikovv.chat.app.struct.Pagination
import by.bashlikovv.chat.app.struct.User
import by.bashlikovv.chat.app.utils.SecurityUtilsImpl
import by.bashlikovv.chat.sources.SourceProviderHolder
import by.bashlikovv.chat.sources.messages.OkHttpMessagesSource
import by.bashlikovv.chat.sources.messages.entities.GetServerMessagesResult
import by.bashlikovv.chat.sources.rooms.OkHttpRoomsSource
import by.bashlikovv.chat.sources.structs.ServerMessage
import by.bashlikovv.chat.sources.structs.ServerRoom
import by.bashlikovv.chat.sources.structs.ServerUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class OkHTTPMessagesRepository : MessagesRepository {

    private val sourceProvider = SourceProviderHolder().sourcesProvider

    private val messagesSource = OkHttpMessagesSource(sourceProvider)

    private val roomsSource = OkHttpRoomsSource(sourceProvider)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getMessagesFromDb(
        chatUiState: ChatUiState
    ): Chat {
        var chatData = chatUiState.chat
        try {
            val getMessagesResult = messagesSource.getRoomMessages(
                chatUiState.chat.token,
                Pagination().getRange()
            )
            val  newValue = getMessagesResult.serverMessages.castListOfMessages()
            chatData = chatData.copy(messages = newValue)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return chatData
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun List<ServerMessage>.castListOfMessages(): List<Message> {
        return this.map {
            var image: Bitmap? = null
            if (!it.image.contains("no image") && it.image.isNotEmpty()) {
                image = messagesSource.getImage(it.image)
            }

            Message(
                value = it.value.decodeToString(),
                time = it.time,
                user = User(
                    userName = it.owner.username,
                    userToken = SecurityUtilsImpl().bytesToString(it.owner.token),
                    userEmail = it.owner.email
                ),
                isRead = it.isRead,
                from = it.from,
                isImage = image != null,
                imageBitmap = image ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            )
        }
    }

    override suspend fun onSendBookmark(bookmark: Message) {
        Repositories.accountsRepository.addBookmark(bookmark = bookmark)
    }

    override suspend fun onDeleteBookmark(bookmark: Message) {
        Repositories.accountsRepository.deleteBookmark(bookmark = bookmark)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSend(
        message: Message,
        chatUiState: ChatUiState,
        me: ServerUser
    ): List<Message> {
        val newValue = chatUiState.chat.messages.toMutableList()
        val msg = Message(
            value = message.value,
            user = message.user,
            time = Calendar.getInstance().time.toString(),
            isRead = true
        )
        newValue.add(msg)
        if (chatUiState.chat.user.userName == "Bookmarks") {
            CoroutineScope(Dispatchers.IO).launch {
                onSendBookmark(msg)
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val room = roomsSource.getRoom(
                    SecurityUtilsImpl().bytesToString(me.token),
                    chatUiState.chat.user.userToken
                )
                messagesSource.sendMessage(
                    ServerMessage(
                        room = room,
                        value = msg.value.encodeToByteArray(),
                        owner = me,
                        image = "no image",
                        file = "no file".encodeToByteArray()
                    ),
                    SecurityUtilsImpl().bytesToString(me.token)
                )
            }
        }

        return newValue
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getMessagesByRoom(
        serverRoom: ServerRoom,
        pagination: Pagination,
        firstUserName: String
    ): GetMessagesResult {
        val result: MutableList<Message> = mutableListOf()
        var count = 0

        try {
            val getMessagesResult = messagesSource.getRoomMessages(
                room = SecurityUtilsImpl().bytesToString(serverRoom.token),
                pagination = pagination.getRange()
            )
            count = getMessagesResult.unreadMessagesCount
            val user = if (serverRoom.user2.username == firstUserName) {
                serverRoom.user1
            } else {
                serverRoom.user2
            }
            result.add(
                Message(
                    value = getMessagesResult.serverMessages.last().value.decodeToString(),
                    user = User(
                        userName = user.username,
                        userToken = SecurityUtilsImpl().bytesToString(user.token),
                        userEmail = user.email
                    ),
                    time = getMessagesResult.serverMessages.last().time,
                    from = SecurityUtilsImpl().bytesToString(user.token)
                )
            )
        } catch (e: Exception) {
            result.add(
                Message(
                    value = "You do not have messages now.",
                    time = ""
                )
            )
        } finally {
            if (result.isEmpty()) {
                result.add(
                    Message(
                        value = "You do not have messages now.",
                        time = ""
                    )
                )
            }
        }

        return GetMessagesResult(messages = result, unreadMessageCount = count)
    }

    override suspend fun readRoomMessages(token: String) {
        messagesSource.readRoomMessages(token)
    }

    override suspend fun getImage(uri: String): UserImage {
        return UserImage(
            messagesSource.getImage(uri),
            userImageUri = Uri.parse(uri)
        )
    }

    override suspend fun getRoomMessages(
        room: String,
        pagination: IntRange
    ): GetServerMessagesResult {
        return messagesSource.getRoomMessages(room, pagination)
    }

    override suspend fun deleteMessage(vararg serverMessage: ServerMessage) {
        messagesSource.deleteMessage(*serverMessage)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun sendImage(
        image: Bitmap,
        room: String,
        owner: String,
        isSignUp: Boolean
    ): String {
        return messagesSource.sendImage(image, room, owner, isSignUp)
    }

}