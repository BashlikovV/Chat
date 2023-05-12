package by.bashlikovv.chat.app.model.users

import by.bashlikovv.chat.app.screens.login.UserImage
import by.bashlikovv.chat.sources.structs.User

interface UsersRepository {

    suspend fun getUserImage(uri: String): UserImage

    suspend fun getUsers(token: String): List<User>

    suspend fun getUser(token: String): User
}