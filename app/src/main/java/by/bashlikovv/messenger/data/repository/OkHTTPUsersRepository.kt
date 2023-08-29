package by.bashlikovv.messenger.data.repository

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import by.bashlikovv.messenger.data.remote.OkHttpAccountsSource
import by.bashlikovv.messenger.data.remote.OkHttpMessagesSource
import by.bashlikovv.messenger.data.remote.OkHttpUsersSource
import by.bashlikovv.messenger.data.remote.base.OkHttpConfig
import by.bashlikovv.messenger.data.remote.model.ServerUser
import by.bashlikovv.messenger.domain.repository.IUsersRepository
import by.bashlikovv.messenger.presentation.view.login.UserImage

class OkHTTPUsersRepository(
    okHttpConfig: OkHttpConfig
) : IUsersRepository {

    private val messagesSource = OkHttpMessagesSource(okHttpConfig)

    private val usersSource = OkHttpUsersSource(okHttpConfig)

    private val accountSource =
        OkHttpAccountsSource(okHttpConfig)

    override suspend fun getUserImage(uri: String): UserImage {
        return UserImage(
            messagesSource.getImage(uri),
            userImageUri = Uri.parse(uri)
        )
    }

    override suspend fun getUsers(token: String): List<ServerUser> {
        val result = usersSource.getAllUsers(token)

        if (result.isEmpty()) { throw Exception() }

        return result
    }

    override suspend fun getUser(token: String): ServerUser {
        return usersSource.getUser(token)
    }

    override suspend fun updateUsername(token: String, newName: String) {
        accountSource.updateUsername(token, newName)
    }

    override suspend fun signIn(email: String, password: String): String {
        return accountSource.signIn(email, password)
    }

    override suspend fun getUsername(token: String): String {
        return accountSource.getUsername(token)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun signUp(email: String, password: String, username: String, image: Bitmap) {
        accountSource.signUp(email, password, username, image)
    }
}