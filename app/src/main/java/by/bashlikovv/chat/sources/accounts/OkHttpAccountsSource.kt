package by.bashlikovv.chat.sources.accounts

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import by.bashlikovv.chat.sources.HttpContract
import by.bashlikovv.chat.sources.accounts.entities.SignInRequestBody
import by.bashlikovv.chat.sources.accounts.entities.SignInResponseBody
import by.bashlikovv.chat.sources.accounts.entities.SignUpRequestBody
import by.bashlikovv.chat.sources.accounts.entities.UpdateUsernameRequestBody
import by.bashlikovv.chat.sources.base.BaseOkHttpSource
import by.bashlikovv.chat.sources.base.OkHttpConfig
import by.bashlikovv.chat.sources.messages.OkHttpMessagesSource
import by.bashlikovv.chat.sources.users.entities.GetUsernameRequestBody
import by.bashlikovv.chat.sources.users.entities.GetUsernameResponseBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request

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
            e.printStackTrace()
            "500 ERROR"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun signUp(email: String, password: String, username: String, image: Bitmap) {
        withContext(Dispatchers.IO) {
            val imageUri = messagesSource.sendImage(image, email, email, true)
            if (imageUri.contains("no image") || imageUri.isEmpty()) {
                return@withContext
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
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
            e.printStackTrace()
            "500 ERROR"
        }
    }

    suspend fun updateUsername(token: String, newName: String): String {
        val updateUsernameRequestBody = UpdateUsernameRequestBody(token, newName)
        val request = Request.Builder()
            .post(updateUsernameRequestBody.toJsonRequestBody())
            .endpoint("/${HttpContract.UrlMethods.UPDATE_USERNAME}")
            .build()
        return try {
            val response = client.newCall(request).suspendEnqueue()
            response.parseJsonResponse<GetUsernameResponseBody>().username
        } catch (e: Exception) {
            e.printStackTrace()
            "500 ERROR"
        }
    }
}