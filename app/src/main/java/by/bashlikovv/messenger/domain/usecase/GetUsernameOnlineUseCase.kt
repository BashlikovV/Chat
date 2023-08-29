package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.domain.repository.IUsersRepository

class GetUsernameOnlineUseCase(private val usersRepository: IUsersRepository) {

    suspend fun execute(token: String): String {
        return usersRepository.getUsername(token)
    }
}