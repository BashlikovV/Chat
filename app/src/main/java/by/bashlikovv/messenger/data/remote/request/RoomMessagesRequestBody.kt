package by.bashlikovv.messenger.data.remote.request

data class RoomMessagesRequestBody(
    val room: String,
    val pagination: IntRange
)