package by.bashlikovv.messenger.data.remote.response

import by.bashlikovv.messenger.data.remote.model.ServerUser

data class GetUserResponseBody(
    val user: ServerUser
)