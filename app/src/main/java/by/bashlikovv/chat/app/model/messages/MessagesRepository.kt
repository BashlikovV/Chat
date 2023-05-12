package by.bashlikovv.chat.app.model.messages

import by.bashlikovv.chat.app.screens.chat.ChatUiState
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.struct.Message

interface MessagesRepository {

    suspend fun getMessagesFromDb(chatUiState: ChatUiState): Chat

    suspend fun List<by.bashlikovv.chat.sources.structs.Message>.castListOfMessages(): List<Message>

    suspend fun onSendBookmark(bookmark: Message)

    suspend fun onDeleteBookmark(bookmark: Message)

    fun onSend(
        message: Message,
        chatUiState: ChatUiState,
        me: by.bashlikovv.chat.sources.structs.User
    ): List<Message>
}