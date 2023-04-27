package by.bashlikovv.chat.sources.users

import by.bashlikovv.chat.sources.HttpContract
import by.bashlikovv.chat.sources.base.BaseOkHttpSource
import by.bashlikovv.chat.sources.base.OkHttpConfig
import by.bashlikovv.chat.sources.structs.User
import by.bashlikovv.chat.sources.users.entities.GetUserRequestBody
import by.bashlikovv.chat.sources.users.entities.GetUserResponseBody
import by.bashlikovv.chat.sources.users.entities.GetUsersRequestBody
import by.bashlikovv.chat.sources.users.entities.GetUsersResponseBody
import okhttp3.Request

class OkHttpUsersSource(
    config: OkHttpConfig
) : BaseOkHttpSource(config) {

    suspend fun getAllUsers(token: String): List<User> {
        return try {
            val getUsersRequestBody = GetUsersRequestBody(
                token = token
            )
            val request = Request.Builder()
                .post(getUsersRequestBody.toJsonRequestBody())
                .endpoint("/${HttpContract.UrlMethods.GET_USERS}")
                .build()
            val response = client.newCall(request).suspendEnqueue()
            response.parseJsonResponse<GetUsersResponseBody>().users
        } catch (e: Exception) {
            e.printStackTrace()
            return listOf(User())
        }
    }

    suspend fun getUser(token: String): User {
        return try {
            val getUserRequestBody = GetUserRequestBody(token)
            val request = Request.Builder()
                .post(getUserRequestBody.toJsonRequestBody())
                .endpoint("/${HttpContract.UrlMethods.GET_USER}")
                .build()
            val response = client.newCall(request).suspendEnqueue()
            response.parseJsonResponse<GetUserResponseBody>().user
        } catch (e: Exception) {
            e.printStackTrace()
            User()
        }
    }
}