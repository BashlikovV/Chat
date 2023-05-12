package by.bashlikovv.chat.app.model.users

import android.net.Uri
import by.bashlikovv.chat.app.screens.login.UserImage
import by.bashlikovv.chat.sources.SourceProviderHolder
import by.bashlikovv.chat.sources.messages.OkHttpMessagesSource
import by.bashlikovv.chat.sources.structs.User
import by.bashlikovv.chat.sources.users.OkHttpUsersSource

class ChatUsersRepository : UsersRepository {

    private val sourceProvider = SourceProviderHolder().sourcesProvider

    private val messagesSource = OkHttpMessagesSource(sourceProvider)

    private val usersSource = OkHttpUsersSource(sourceProvider)

    override suspend fun getUserImage(uri: String): UserImage {
        return UserImage(
            messagesSource.getImage(uri),
            userImageUri = Uri.parse(uri)
        )
    }

    override suspend fun getUsers(token: String): List<User> {
        return usersSource.getAllUsers(token)
    }

    override suspend fun getUser(token: String): User {
        return usersSource.getUser(token)
    }
}