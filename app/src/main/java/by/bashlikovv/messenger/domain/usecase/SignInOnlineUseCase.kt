package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.domain.repository.IUsersRepository

class SignInOnlineUseCase(private val usersRepository: IUsersRepository) {

    suspend fun execute(email: String, password: String): String {
        return usersRepository.signIn(email, password)
    }
}