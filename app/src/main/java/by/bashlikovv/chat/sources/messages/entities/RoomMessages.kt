package by.bashlikovv.chat.sources.messages.entities

import by.bashlikovv.chat.sources.structs.Message

data class RoomMessagesRequestBody(
    val room: String,
    val pagination: IntRange
)

data class RoomMessagesResponseBody(
    val messages: List<Message>,
    val unreadMessagesCount: Int
)

data class GetMessagesResult(
    val messages: List<Message> = listOf(),
    val unreadMessagesCount: Int = 0
)