package by.bashlikovv.chat.sources.messages.entities

import by.bashlikovv.chat.sources.structs.ServerMessage

data class RoomMessagesRequestBody(
    val room: String,
    val pagination: IntRange
)

data class RoomMessagesResponseBody(
    val messages: List<ServerMessage>,
    val unreadMessagesCount: Int
)