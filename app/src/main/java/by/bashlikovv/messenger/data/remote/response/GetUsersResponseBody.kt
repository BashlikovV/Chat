package by.bashlikovv.messenger.data.remote.response

import by.bashlikovv.messenger.data.remote.model.ServerUser
import com.google.gson.annotations.SerializedName

data class GetUsersResponseBody(
    @SerializedName("users") val users: List<ServerUser>
)