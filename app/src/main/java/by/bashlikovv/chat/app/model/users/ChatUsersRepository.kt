package by.bashlikovv.chat.app.model.users

import android.net.Uri
import by.bashlikovv.chat.app.screens.login.UserImage
import by.bashlikovv.chat.sources.SourceProviderHolder
import by.bashlikovv.chat.sources.messages.OkHttpMessagesSource

class ChatUsersRepository : UsersRepository {

    private val sourceProvider = SourceProviderHolder().sourcesProvider

    private val messagesSource = OkHttpMessagesSource(sourceProvider)

    override suspend fun getUserImage(uri: String): UserImage {
        return UserImage(
            messagesSource.getImage(
                uri
            ),
            userImageUri = Uri.parse(uri)
        )
    }
}