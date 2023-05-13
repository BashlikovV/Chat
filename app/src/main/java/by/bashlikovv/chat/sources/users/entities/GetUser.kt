package by.bashlikovv.chat.sources.users.entities

import by.bashlikovv.chat.sources.structs.ServerUser

data class GetUserRequestBody(
    val token: String
)

data class GetUserResponseBody(
    val user: ServerUser
)