package by.bashlikovv.messenger.domain.repository

import android.graphics.Bitmap
import by.bashlikovv.messenger.data.remote.model.GetMessagesResult
import by.bashlikovv.messenger.data.remote.model.GetServerMessagesResult
import by.bashlikovv.messenger.data.remote.model.ServerMessage
import by.bashlikovv.messenger.data.remote.model.ServerRoom
import by.bashlikovv.messenger.data.remote.model.ServerUser
import by.bashlikovv.messenger.domain.model.Chat
import by.bashlikovv.messenger.domain.model.Message
import by.bashlikovv.messenger.domain.model.Pagination
import by.bashlikovv.messenger.presentation.view.chat.ChatUiState

interface IMessagesRepository {

    suspend fun getMessages(chatUiState: ChatUiState): Chat

    suspend fun List<ServerMessage>.castListOfMessages(): List<Message>

    suspend fun onSendBookmark(bookmark: Message)

    suspend fun onDeleteBookmark(bookmark: Message)

    suspend fun sendMessage(
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

    suspend fun getRoomMessages(
        room: String,
        pagination: IntRange
    ): GetServerMessagesResult

    suspend fun deleteMessage(serverMessage: List<ServerMessage>)

    suspend fun sendImage(image: Bitmap, room: String, owner: String, isSignUp: Boolean): String
}