package by.bashlikovv.chat.app.model.users

import android.graphics.Bitmap
import by.bashlikovv.chat.app.screens.login.UserImage
import by.bashlikovv.chat.sources.structs.User

interface UsersRepository {

    suspend fun getUserImage(uri: String): UserImage

    suspend fun getUsers(token: String): List<User>

    suspend fun getUser(token: String): User

    suspend fun updateUsername(token: String, newName: String)

    suspend fun signIn(email: String, password: String): String

    suspend fun getUsername(token: String): String

    suspend fun signUp(email: String, password: String, username: String, image: Bitmap)
}