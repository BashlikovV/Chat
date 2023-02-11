package by.bashlikovv.chat.chat.message

import by.bashlikovv.chat.chat.message.item.MessageItemUiState

/**
 * [MessageItemUiState] contains data to present list of messages in chat.
 * * [userImage] -> image of current companion
 * * [userName] -> name of current companion
 * * [isOnline] -> is current companion in chat
 * * [messages] -> messages in current chat
 * */

data class MessagesListUiSate(
    val userImage: Int = 0,
    val userName: String = "",
    val isOnline: Boolean = false,
    val messages: List<MessageItemUiState> = listOf(MessageItemUiState()),
    val input: String = "Message"
)