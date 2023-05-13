package by.bashlikovv.chat.sources.messages.entities

import by.bashlikovv.chat.sources.structs.ServerMessage

data class DeleteMessageRequestBody(
    val message: ServerMessage
)