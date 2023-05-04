package by.bashlikovv.chat.sources.messages.entities

data class ReadMessagesRequestBody(
    val room: String
)

data class ReadMessagesResponseBody(
    val result: String
)