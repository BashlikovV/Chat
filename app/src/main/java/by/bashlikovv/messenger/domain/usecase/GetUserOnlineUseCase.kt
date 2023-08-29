package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.remote.model.ServerUser
import by.bashlikovv.messenger.domain.repository.IUsersRepository

class GetUserOnlineUseCase(private val usersRepository: IUsersRepository) {

    suspend fun execute(token: String): ServerUser {
        return usersRepository.getUser(token)
    }
}