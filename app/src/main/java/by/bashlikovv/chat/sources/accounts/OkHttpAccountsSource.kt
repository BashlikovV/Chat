package by.bashlikovv.chat.sources.accounts

import by.bashlikovv.chat.sources.accounts.entities.GetUsersResponseBody
import by.bashlikovv.chat.sources.base.entities.User
import by.bashlikovv.chat.sources.base.BaseOkHttpSource
import by.bashlikovv.chat.sources.base.OkHttpConfig
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
        val response = client.newCall(request).suspendEnqueue()
        return response.parseJsonResponse<SignInResponseBody>().token
    }

    suspend fun signUp(email: String, password: String, username: String) {
        val signUpRequestBody = SignUpRequestBody(
            username, email, password
        )
        val request = Request.Builder()
            .post(signUpRequestBody.toJsonRequestBody())
            .endpoint("/sign-up")
            .build()
        client.newCall(request).suspendEnqueue()
    }
}