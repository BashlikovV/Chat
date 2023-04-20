package by.bashlikovv.chat.sources.accounts

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import by.bashlikovv.chat.sources.HttpContract
import by.bashlikovv.chat.sources.base.BaseOkHttpSource
import by.bashlikovv.chat.sources.base.OkHttpConfig
import by.bashlikovv.chat.sources.users.entities.GetUsernameRequestBody
import by.bashlikovv.chat.sources.users.entities.GetUsernameResponseBody
import okhttp3.Request
import by.bashlikovv.chat.sources.accounts.entities.SignInRequestBody
import by.bashlikovv.chat.sources.accounts.entities.SignInResponseBody
import by.bashlikovv.chat.sources.accounts.entities.SignUpRequestBody
import by.bashlikovv.chat.sources.messages.OkHttpMessagesSource

class OkHttpAccountsSource(
    config: OkHttpConfig,
    private val messagesSource: OkHttpMessagesSource = OkHttpMessagesSource(config)
) : BaseOkHttpSource(config) {

    suspend fun signIn(email: String, password: String): String {
        val signInRequestBody = SignInRequestBody(email, password)
        val request = Request.Builder()
            .post(signInRequestBody.toJsonRequestBody())
            .endpoint("/${HttpContract.UrlMethods.SIGN_IN}")
            .build()
        return try {
            val response = client.newCall(request).suspendEnqueue()
            response.parseJsonResponse<SignInResponseBody>().token
        } catch (e: Exception) {
            "500 ERROR"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun signUp(email: String, password: String, username: String, image: Bitmap) {
        val imageUri = messagesSource.sendImage(image, email, email, true)
        if (!imageUri.contains("/home")) {
            return
        }
        val signUpRequestBody = SignUpRequestBody(
            username, email, password, imageUri
        )
        val request = Request.Builder()
            .post(signUpRequestBody.toJsonRequestBody())
            .endpoint("/${HttpContract.UrlMethods.SIGN_UP}")
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
            .endpoint("/${HttpContract.UrlMethods.GET_USERNAME}")
            .build()
        return try {
            val response = client.newCall(request).suspendEnqueue()
            response.parseJsonResponse<GetUsernameResponseBody>().username
        } catch (e: Exception) {
            "500 ERROR"
        }
    }
}