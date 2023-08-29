package by.bashlikovv.messenger.domain.repository

import android.graphics.Bitmap
import by.bashlikovv.messenger.data.remote.model.ServerUser
import by.bashlikovv.messenger.presentation.view.login.UserImage

interface IUsersRepository {

    suspend fun getUserImage(uri: String): UserImage

    suspend fun getUsers(token: String): List<ServerUser>

    suspend fun getUser(token: String): ServerUser

    suspend fun updateUsername(token: String, newName: String)

    suspend fun signIn(email: String, password: String): String

    suspend fun getUsername(token: String): String

    suspend fun signUp(email: String, password: String, username: String, image: Bitmap)
}