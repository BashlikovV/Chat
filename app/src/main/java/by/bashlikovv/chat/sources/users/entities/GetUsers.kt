package by.bashlikovv.chat.sources.users.entities

import by.bashlikovv.chat.sources.structs.ServerUser
import com.google.gson.annotations.SerializedName

data class GetUsersRequestBody(val token: String)

data class GetUsersResponseBody(@SerializedName("users") val users: List<ServerUser>)