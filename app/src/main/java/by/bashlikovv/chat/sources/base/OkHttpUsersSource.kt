package by.bashlikovv.chat.sources.base

import by.bashlikovv.chat.sources.accounts.entities.GetUsersResponseBody
import by.bashlikovv.chat.sources.base.entities.User
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