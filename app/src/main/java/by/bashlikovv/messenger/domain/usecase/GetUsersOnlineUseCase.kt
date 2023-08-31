package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.remote.model.ServerUser
import by.bashlikovv.messenger.data.repository.OkHTTPUsersRepository

class GetUsersOnlineUseCase(private val usersRepository: OkHTTPUsersRepository) {

    suspend fun execute(token: String): List<ServerUser> {
        return usersRepository.getUsers(token)
    }
}