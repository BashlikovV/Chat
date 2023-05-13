package by.bashlikovv.chat.sources.rooms.entities

import by.bashlikovv.chat.sources.structs.ServerRoom

data class GetRoomRequestBody(
    val user1: String,
    val user2: String
)

data class GetRoomResponseBody(
    val room: ServerRoom
)