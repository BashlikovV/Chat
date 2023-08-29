package by.bashlikovv.messenger.domain.usecase

import android.graphics.Bitmap
import by.bashlikovv.messenger.domain.repository.IUsersRepository

class SignUpOnlineUseCase(private val usersRepository: IUsersRepository) {

    suspend fun execute(email: String, password: String, username: String, image: Bitmap) {
        usersRepository.signUp(email, password, username, image)
    }
}