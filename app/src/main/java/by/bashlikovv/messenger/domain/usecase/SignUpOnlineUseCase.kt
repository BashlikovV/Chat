package by.bashlikovv.messenger.domain.usecase

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import by.bashlikovv.messenger.data.repository.OkHTTPUsersRepository

class SignUpOnlineUseCase(private val usersRepository: OkHTTPUsersRepository) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun execute(email: String, password: String, username: String, image: Bitmap) {
        usersRepository.signUp(email, password, username, image)
    }
}