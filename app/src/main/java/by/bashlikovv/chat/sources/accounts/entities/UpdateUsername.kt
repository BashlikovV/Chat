package by.bashlikovv.chat.sources.accounts.entities

data class UpdateUsernameRequestBody(
    val token: String,
    val newName: String
)

data class UpdateUsernameResponseBody(
    val result: String
)