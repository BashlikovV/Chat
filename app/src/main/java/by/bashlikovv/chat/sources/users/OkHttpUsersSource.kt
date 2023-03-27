package by.bashlikovv.chat.sources.users

import by.bashlikovv.chat.sources.users.entities.GetUsersResponseBody
import by.bashlikovv.chat.sources.base.BaseOkHttpSource
import by.bashlikovv.chat.sources.base.OkHttpConfig
import by.bashlikovv.chat.sources.structs.User
import okhttp3.Request

class OkHttpUsersSource(
    config: OkHttpConfig
) : BaseOkHttpSource(config) {

    suspend fun getAllUsers(): List<User> {
        val request = Request.Builder()
            .endpoint("/get-users")
            .build()
        val response = client.newCall(request).suspendEnqueue()
        return response.parseJsonResponse<GetUsersResponseBody>().users
    }
}