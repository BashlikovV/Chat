package by.bashlikovv.chat.app.screens.messenger

import by.bashlikovv.chat.app.struct.Chat

data class MessengerUiState(
    val chats: List<Chat> = emptyList(),
    val visible: Boolean = false,
    val selectedItem: Chat = Chat(),
    val darkTheme: Boolean = true,
    val expanded: Boolean = false,
    val newChat: Boolean = false,
    val searchInput: String = ""
)