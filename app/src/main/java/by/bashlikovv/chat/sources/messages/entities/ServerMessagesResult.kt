package by.bashlikovv.chat.sources.messages.entities

import by.bashlikovv.chat.sources.structs.ServerMessage

data class GetServerMessagesResult(
    val serverMessages: List<ServerMessage> = listOf(),
    val unreadMessagesCount: Int = 0
)