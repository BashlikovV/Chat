package by.bashlikovv.chat.chat.list

data class ChatListUiState(
    val image: Int = 0,
    val name: String = "",
    val displayedMessage: String = "",
    val time: String = "",
    val unreadMessagesCount: Int = 0
)
