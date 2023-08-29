package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.remote.model.ServerUser
import by.bashlikovv.messenger.domain.repository.IUsersRepository

class GetUsersOnlineUseCase(private val usersRepository: IUsersRepository) {

    suspend fun execute(token: String): List<ServerUser> {
        return usersRepository.getUsers(token)
    }
}