package by.bashlikovv.chat.sources.accounts.entities

import by.bashlikovv.chat.sources.base.entities.User

data class GetUsersRequestBody(val token: String)

data class GetUsersResponseBody(val users: List<User>)