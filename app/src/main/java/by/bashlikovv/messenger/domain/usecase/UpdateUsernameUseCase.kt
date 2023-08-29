package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.domain.repository.IAccountsRepository
import by.bashlikovv.messenger.domain.repository.IUsersRepository

class UpdateUsernameUseCase(
    private val usersRepository: IUsersRepository,
    private val accountsRepository: IAccountsRepository
) {

    suspend fun execute(name: String, token: String) {
        usersRepository.updateUsername(token, name)
        accountsRepository.updateAccountUsername(name)
    }
}