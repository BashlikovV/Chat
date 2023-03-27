package by.bashlikovv.chat.sources.users.entities

data class GetUsernameRequestBody(
    val token: String
)

data class GetUsernameResponseBody(
    val username: String
)