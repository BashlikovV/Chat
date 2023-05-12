package by.bashlikovv.chat.app.screens.chat

import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.struct.Message
import by.bashlikovv.chat.app.struct.User

data class ChatUiState(
    val chat: Chat = Chat(),
    val inputHeight: Int = 0,
    val usersData: List<User> = emptyList(),
    val isCanSend: Boolean = false,
    val selectedMessage: Message = Message()
)