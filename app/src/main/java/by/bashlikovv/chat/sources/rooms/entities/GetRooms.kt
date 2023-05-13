package by.bashlikovv.chat.sources.rooms.entities

import by.bashlikovv.chat.sources.structs.ServerRoom

data class GetRoomsRequestBody(
    val user: String
)

data class GetRoomsResponseBody(
    val rooms: List<ServerRoom>
)