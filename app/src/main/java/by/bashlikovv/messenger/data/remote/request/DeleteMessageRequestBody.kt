package by.bashlikovv.messenger.data.remote.request

import by.bashlikovv.messenger.data.remote.model.ServerMessage

data class DeleteMessageRequestBody(
    val messages: List<ServerMessage>
)