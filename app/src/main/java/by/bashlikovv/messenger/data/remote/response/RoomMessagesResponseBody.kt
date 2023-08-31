package by.bashlikovv.messenger.data.remote.response

import by.bashlikovv.messenger.data.remote.model.ServerMessage

data class RoomMessagesResponseBody(
    val messages: List<ServerMessage>,
    val unreadMessagesCount: Int
)