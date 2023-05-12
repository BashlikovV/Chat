package by.bashlikovv.chat.app.model.messages

import android.graphics.Bitmap
import by.bashlikovv.chat.app.screens.chat.ChatUiState
import by.bashlikovv.chat.app.screens.login.UserImage
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.struct.Message
import by.bashlikovv.chat.app.struct.Pagination
import by.bashlikovv.chat.sources.structs.Room

interface MessagesRepository {

    data class GetMessagesResult(
        val messages: List<Message> = listOf(),
        val unreadMessageCount: Int = 0
    )

    suspend fun getMessagesFromDb(chatUiState: ChatUiState): Chat

    suspend fun List<by.bashlikovv.chat.sources.structs.Message>.castListOfMessages(): List<Message>

    suspend fun onSendBookmark(bookmark: Message)

    suspend fun onDeleteBookmark(bookmark: Message)

    fun onSend(
        message: Message,
        chatUiState: ChatUiState,
        me: by.bashlikovv.chat.sources.structs.User
    ): List<Message>

    suspend fun getMessagesByRoom(
        room: Room,
        pagination: Pagination,
        firstUserName: String
    ): GetMessagesResult

    suspend fun readRoomMessages(token: String)

    suspend fun getImage(uri: String): UserImage

    suspend fun getRoomMessages(
        room: String,
        pagination: IntRange
    ): by.bashlikovv.chat.sources.messages.entities.GetMessagesResult

    suspend fun deleteMessage(message: by.bashlikovv.chat.sources.structs.Message)

    suspend fun sendImage(image: Bitmap, room: String, owner: String, isSignUp: Boolean): String
}