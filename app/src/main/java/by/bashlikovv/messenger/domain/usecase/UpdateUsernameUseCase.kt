package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.repository.OkHTTPUsersRepository
import by.bashlikovv.messenger.data.repository.SQLiteAccountsRepository

class UpdateUsernameUseCase(
    private val usersRepository: OkHTTPUsersRepository,
    private val accountsRepository: SQLiteAccountsRepository
) {

    suspend fun execute(name: String, token: String) {
        usersRepository.updateUsername(token, name)
        accountsRepository.updateAccountUsername(name)
    }
}