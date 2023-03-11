package by.bashlikovv.chat.screens.chat

import by.bashlikovv.chat.struct.Chat
import by.bashlikovv.chat.struct.Message
import by.bashlikovv.chat.struct.User

data class ChatUiState(
    val chat: Chat = Chat(),
    val textInputState: String = "",
    val inputHeight: Int = 0,
    val usersData: List<User> = emptyList(),
    val isCanSend: Boolean = false,
    val selectedMessage: Message = Message()
)