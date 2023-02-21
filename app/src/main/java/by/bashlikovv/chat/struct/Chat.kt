package by.bashlikovv.chat.struct

data class Chat(
    val user: User = User(),
    val messages: List<Message> = emptyList(),
    val time: String = "",
    val count: Int = 0
)