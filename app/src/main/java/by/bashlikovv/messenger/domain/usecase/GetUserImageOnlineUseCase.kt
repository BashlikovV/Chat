package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.repository.OkHTTPUsersRepository

class GetUserImageOnlineUseCase(private val usersRepository: OkHTTPUsersRepository) {

    suspend fun execute(uri: String): String {
        return usersRepository.getUserImage(uri)
    }
}