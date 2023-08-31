package by.bashlikovv.messenger.data.repository

import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import by.bashlikovv.messenger.data.remote.OkHttpMessagesSource
import by.bashlikovv.messenger.data.remote.OkHttpRoomsSource
import by.bashlikovv.messenger.data.remote.base.OkHttpConfig
import by.bashlikovv.messenger.data.remote.model.GetMessagesResult
import by.bashlikovv.messenger.data.remote.model.GetServerMessagesResult
import by.bashlikovv.messenger.data.remote.model.ServerMessage
import by.bashlikovv.messenger.data.remote.model.ServerRoom
import by.bashlikovv.messenger.data.remote.model.ServerUser
import by.bashlikovv.messenger.domain.model.Chat
import by.bashlikovv.messenger.domain.model.Message
import by.bashlikovv.messenger.domain.model.Pagination
import by.bashlikovv.messenger.domain.model.User
import by.bashlikovv.messenger.domain.repository.IMessagesRepository
import by.bashlikovv.messenger.presentation.view.chat.ChatUiState
import by.bashlikovv.messenger.utils.SecurityUtilsImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class MessagesRepository(
    okHttpConfig: OkHttpConfig,
    private val cm: ConnectivityManager?,
    private val accountsRepository: SQLiteAccountsRepository
): IMessagesRepository {

    private val messagesSource: OkHttpMessagesSource = OkHttpMessagesSource(okHttpConfig)

    private val roomsSource: OkHttpRoomsSource = OkHttpRoomsSource(okHttpConfig)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getMessages(
        chatUiState: ChatUiState
    ): Chat {
        return if (isConnected()) {
            getMessagesOnline(chatUiState)
        } else {
            getMessagesOffline(chatUiState)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getMessagesOnline(chatUiState: ChatUiState): Chat {
        var chatData = chatUiState.chat
        try {
            val getMessagesResult = messagesSource.getRoomMessages(
                chatUiState.chat.token,
                Pagination().getRange()
            )
            val newValue = getMessagesResult.serverMessages.castListOfMessages()
            chatData = chatData.copy(messages = newValue)
        } catch (_: Exception) {  }

        return chatData
    }

    private suspend fun getMessagesOffline(chatUiState: ChatUiState): Chat {
        TODO()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun List<ServerMessage>.castListOfMessages(): List<Message> {
        return this.map {
            var image: String? = null
            if (!it.image.contains("no image") && it.image.isNotEmpty()) {
                image = it.image
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
                imageBitmap = image ?: "no image"
            )
        }
    }

    override suspend fun onSendBookmark(bookmark: Message) {
        accountsRepository.addBookmark(bookmark)
    }

    override suspend fun onDeleteBookmark(bookmark: Message) {
        accountsRepository.deleteBookmark(bookmark)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun sendMessage(
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

    override suspend fun getRoomMessages(
        room: String,
        pagination: IntRange
    ): GetServerMessagesResult {
        return messagesSource.getRoomMessages(room, pagination)
    }

    override suspend fun deleteMessage(serverMessage: List<ServerMessage>) {
        messagesSource.deleteMessage(serverMessage)
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

    private fun isConnected(): Boolean {
        var isConnected = false
        cm?.run {
            cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                isConnected = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }
        }

        return isConnected
    }
}