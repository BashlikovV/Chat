package by.bashlikovv.chat.app.model.messages

import android.graphics.Bitmap
import by.bashlikovv.chat.app.model.messages.entities.GetMessagesResult
import by.bashlikovv.chat.app.screens.chat.ChatUiState
import by.bashlikovv.chat.app.screens.login.UserImage
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.struct.Message
import by.bashlikovv.chat.app.struct.Pagination
import by.bashlikovv.chat.sources.structs.ServerMessage
import by.bashlikovv.chat.sources.structs.ServerRoom
import by.bashlikovv.chat.sources.structs.ServerUser

interface MessagesRepository {

    suspend fun getMessagesFromDb(chatUiState: ChatUiState): Chat

    suspend fun List<ServerMessage>.castListOfMessages(): List<Message>

    suspend fun onSendBookmark(bookmark: Message)

    suspend fun onDeleteBookmark(bookmark: Message)

    fun onSend(
        message: Message,
        chatUiState: ChatUiState,
        me: ServerUser
    ): List<Message>

    suspend fun getMessagesByRoom(
        serverRoom: ServerRoom,
        pagination: Pagination,
        firstUserName: String
    ): GetMessagesResult

    suspend fun readRoomMessages(token: String)

    suspend fun getImage(uri: String): UserImage

    suspend fun getRoomMessages(
        room: String,
        pagination: IntRange
    ): by.bashlikovv.chat.sources.messages.entities.GetServerMessagesResult

    suspend fun deleteMessage(vararg serverMessage: ServerMessage)

    suspend fun sendImage(image: Bitmap, room: String, owner: String, isSignUp: Boolean): String
}