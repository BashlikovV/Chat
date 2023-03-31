package by.bashlikovv.chat.sources.accounts

import by.bashlikovv.chat.sources.base.BaseOkHttpSource
import by.bashlikovv.chat.sources.base.OkHttpConfig
import by.bashlikovv.chat.sources.users.entities.GetUsernameRequestBody
import by.bashlikovv.chat.sources.users.entities.GetUsernameResponseBody
import okhttp3.Request
import server.entities.SignInRequestBody
import server.entities.SignInResponseBody
import server.entities.SignUpRequestBody

class OkHttpAccountsSource(
    config: OkHttpConfig
) : BaseOkHttpSource(config) {

    suspend fun signIn(email: String, password: String): String {
        val signInRequestBody = SignInRequestBody(email, password)
        val request = Request.Builder()
            .post(signInRequestBody.toJsonRequestBody())
            .endpoint("/sign-in")
            .build()
        return try {
            val response = client.newCall(request).suspendEnqueue()
            response.parseJsonResponse<SignInResponseBody>().token
        } catch (e: Exception) {
            "500 ERROR"
        }
    }

    suspend fun signUp(email: String, password: String, username: String) {
        val signUpRequestBody = SignUpRequestBody(
            username, email, password
        )
        val request = Request.Builder()
            .post(signUpRequestBody.toJsonRequestBody())
            .endpoint("/sign-up")
            .build()
        try {
            client.newCall(request).suspendEnqueue()
        } catch (_: Exception) {
        }
    }

    suspend fun getUsername(token: String): String {
        val getUsernameRequestBody = GetUsernameRequestBody(token)
        val request = Request.Builder()
            .post(getUsernameRequestBody.toJsonRequestBody())
            .endpoint("/get-username")
            .build()
        return try {
            val response = client.newCall(request).suspendEnqueue()
            response.parseJsonResponse<GetUsernameResponseBody>().username
        } catch (e: Exception) {
            "500 ERROR"
        }
    }
}