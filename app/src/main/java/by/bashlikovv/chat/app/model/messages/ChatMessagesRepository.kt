package by.bashlikovv.chat.app.model.messages

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import by.bashlikovv.chat.Repositories
import by.bashlikovv.chat.app.screens.chat.ChatUiState
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.struct.Pagination
import by.bashlikovv.chat.app.struct.User
import by.bashlikovv.chat.app.utils.SecurityUtilsImpl
import by.bashlikovv.chat.sources.SourceProviderHolder
import by.bashlikovv.chat.sources.messages.OkHttpMessagesSource
import by.bashlikovv.chat.sources.rooms.OkHttpRoomsSource
import by.bashlikovv.chat.sources.structs.Message
import by.bashlikovv.chat.sources.structs.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class ChatMessagesRepository : MessagesRepository {

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
            val  newValue = getMessagesResult.messages.castListOfMessages()
            chatData = chatData.copy(messages = newValue)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return chatData
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun List<Message>.castListOfMessages(): List<by.bashlikovv.chat.app.struct.Message> {
        return this.map {
            var image: Bitmap? = null
            if (!it.image.contains("no image") && it.image.isNotEmpty()) {
                image = messagesSource.getImage(it.image)
            }

            by.bashlikovv.chat.app.struct.Message(
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

    override suspend fun onSendBookmark(bookmark: by.bashlikovv.chat.app.struct.Message) {
        Repositories.accountsRepository.addBookmark(bookmark = bookmark)
    }

    override suspend fun onDeleteBookmark(bookmark: by.bashlikovv.chat.app.struct.Message) {
        Repositories.accountsRepository.deleteBookmark(bookmark = bookmark)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSend(
        message: by.bashlikovv.chat.app.struct.Message,
        chatUiState: ChatUiState,
        me: by.bashlikovv.chat.sources.structs.User
    ): List<by.bashlikovv.chat.app.struct.Message> {
        val newValue = chatUiState.chat.messages.toMutableList()
        val msg = by.bashlikovv.chat.app.struct.Message(
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
                    Message(
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
        room: Room,
        pagination: Pagination,
        firstUserName: String
    ): MessagesRepository.GetMessagesResult {
        val result: MutableList<by.bashlikovv.chat.app.struct.Message> = mutableListOf()
        var count = 0

        try {
            val getMessagesResult = messagesSource.getRoomMessages(
                room = SecurityUtilsImpl().bytesToString(room.token),
                pagination = pagination.getRange()
            )
            count = getMessagesResult.unreadMessagesCount
            val user = if (room.user2.username == firstUserName) {
                room.user1
            } else {
                room.user2
            }
            result.add(
                by.bashlikovv.chat.app.struct.Message(
                    value = getMessagesResult.messages.last().value.decodeToString(),
                    user = User(
                        userName = user.username,
                        userToken = SecurityUtilsImpl().bytesToString(user.token),
                        userEmail = user.email
                    ),
                    time = getMessagesResult.messages.last().time,
                    from = SecurityUtilsImpl().bytesToString(user.token)
                )
            )
        } catch (e: Exception) {
            result.add(
                by.bashlikovv.chat.app.struct.Message(
                    value = "You do not have messages now.",
                    time = ""
                )
            )
        } finally {
            if (result.isEmpty()) {
                result.add(
                    by.bashlikovv.chat.app.struct.Message(
                        value = "You do not have messages now.",
                        time = ""
                    )
                )
            }
        }

        return MessagesRepository.GetMessagesResult(messages = result, unreadMessageCount = count)
    }

}