package by.bashlikovv.chat.sources.messages.entities

import by.bashlikovv.chat.sources.structs.Message

data class DeleteMessageRequestBody(
    val message: Message
)

data class DeleteMessageResponseBody(
    val result: String
)