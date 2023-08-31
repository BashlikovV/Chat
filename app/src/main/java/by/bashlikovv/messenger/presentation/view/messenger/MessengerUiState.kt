package by.bashlikovv.messenger.presentation.view.messenger

import by.bashlikovv.messenger.domain.model.Chat

data class MessengerUiState(
    val chats: List<Chat> = emptyList(),
    val visible: Boolean = false,
    val selectedItem: Chat = Chat(),
    val darkTheme: Boolean = true,
    val expanded: Boolean = false,
    val newChat: Boolean = false,
    val searchInput: String = ""
)