package by.bashlikovv.chat.app.model.users

import by.bashlikovv.chat.app.screens.login.UserImage

interface UsersRepository {

    suspend fun getUserImage(uri: String): UserImage
}