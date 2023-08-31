package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.remote.model.ServerUser
import by.bashlikovv.messenger.data.repository.OkHTTPUsersRepository

class GetUserOnlineUseCase(private val usersRepository: OkHTTPUsersRepository) {

    suspend fun execute(token: String): ServerUser {
        return usersRepository.getUser(token)
    }
}