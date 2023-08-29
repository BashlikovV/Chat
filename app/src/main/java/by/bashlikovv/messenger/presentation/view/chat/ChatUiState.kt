package by.bashlikovv.messenger.presentation.view.chat

import by.bashlikovv.messenger.domain.model.Chat
import by.bashlikovv.messenger.domain.model.User

data class ChatUiState(
    val chat: Chat = Chat(),
    val inputHeight: Int = 0,
    val usersData: List<User> = emptyList(),
    val isCanSend: Boolean = false
)