package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.repository.OkHTTPUsersRepository

class GetUsernameOnlineUseCase(private val usersRepository: OkHTTPUsersRepository) {

    suspend fun execute(token: String): String {
        return usersRepository.getUsername(token)
    }
}