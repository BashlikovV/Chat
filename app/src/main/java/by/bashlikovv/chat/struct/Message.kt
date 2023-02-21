package by.bashlikovv.chat.struct

data class Message(
    val value: String = "",
    val user: User = User(),
    val time: String = "",
    val isRead: Boolean = false
)
