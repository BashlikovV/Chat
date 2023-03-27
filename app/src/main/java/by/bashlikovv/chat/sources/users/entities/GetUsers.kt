package by.bashlikovv.chat.sources.users.entities

import by.bashlikovv.chat.sources.structs.User

data class GetUsersRequestBody(val token: String)

data class GetUsersResponseBody(val users: List<User>)