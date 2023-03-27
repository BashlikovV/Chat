package by.bashlikovv.chat.sources.messages.entities

import by.bashlikovv.chat.sources.structs.Message

data class RoomMessagesRequestBody(
    val user1: String,
    val user2: String
)

data class RoomMessagesResponseBody(
    val messages: List<Message>
)