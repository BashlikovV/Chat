package by.bashlikovv.chat.app.model.messages.entities

import by.bashlikovv.chat.app.struct.Message

data class GetMessagesResult(
    val messages: List<Message> = listOf(),
    val unreadMessageCount: Int = 0
)