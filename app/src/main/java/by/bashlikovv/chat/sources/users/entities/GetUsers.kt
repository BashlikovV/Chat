package by.bashlikovv.chat.sources.users.entities

import by.bashlikovv.chat.sources.structs.ServerUser

data class GetUsersRequestBody(val token: String)

data class GetUsersResponseBody(val users: List<ServerUser>)