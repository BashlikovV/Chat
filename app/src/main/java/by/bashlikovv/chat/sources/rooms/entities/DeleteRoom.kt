package by.bashlikovv.chat.sources.rooms.entities

data class DeleteRoomRequestBody(
    val user1: String,
    val user2: String
)

data class DeleteRoomResponseBody(
    val result: String
)