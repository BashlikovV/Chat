package by.bashlikovv.chat.app.model.users

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import by.bashlikovv.chat.app.screens.login.UserImage
import by.bashlikovv.chat.sources.SourceProviderHolder
import by.bashlikovv.chat.sources.accounts.OkHttpAccountsSource
import by.bashlikovv.chat.sources.messages.OkHttpMessagesSource
import by.bashlikovv.chat.sources.structs.ServerUser
import by.bashlikovv.chat.sources.users.OkHttpUsersSource

class OkHTTPUsersRepository : UsersRepository {

    private val sourceProvider = SourceProviderHolder().sourcesProvider

    private val messagesSource = OkHttpMessagesSource(sourceProvider)

    private val usersSource = OkHttpUsersSource(sourceProvider)

    private val accountSource = OkHttpAccountsSource(sourceProvider)

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