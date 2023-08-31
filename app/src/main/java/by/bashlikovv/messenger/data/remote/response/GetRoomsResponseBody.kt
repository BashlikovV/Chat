package by.bashlikovv.messenger.data.remote.response

import by.bashlikovv.messenger.data.remote.model.ServerRoom

data class GetRoomsResponseBody(
    val rooms: List<ServerRoom>
)