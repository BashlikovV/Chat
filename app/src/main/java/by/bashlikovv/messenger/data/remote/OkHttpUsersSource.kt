package by.bashlikovv.messenger.data.remote

import by.bashlikovv.messenger.data.remote.base.BaseOkHttpSource
import by.bashlikovv.messenger.data.remote.base.OkHttpConfig
import by.bashlikovv.messenger.data.remote.contract.HttpContract
import by.bashlikovv.messenger.data.remote.model.ServerUser
import by.bashlikovv.messenger.data.remote.request.GetUserRequestBody
import by.bashlikovv.messenger.data.remote.request.GetUsersRequestBody
import by.bashlikovv.messenger.data.remote.response.GetUserResponseBody
import by.bashlikovv.messenger.data.remote.response.GetUsersResponseBody
import okhttp3.Request

class OkHttpUsersSource(
    config: OkHttpConfig
) : BaseOkHttpSource(config) {

    suspend fun getAllUsers(token: String): List<ServerUser> {
        return try {
            val getUsersRequestBody = GetUsersRequestBody(token = token)
            val request = Request.Builder()
                .post(getUsersRequestBody.toJsonRequestBody())
                .endpoint("/${HttpContract.UrlMethods.GET_USERS}")
                .build()
            val response = client.newCall(request).suspendEnqueue()
            response.parseJsonResponse<GetUsersResponseBody>().users
        } catch (e: Exception) {
            e.printStackTrace()
            return listOf(ServerUser())
        }
    }

    suspend fun getUser(token: String): ServerUser {
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
            ServerUser()
        }
    }
}