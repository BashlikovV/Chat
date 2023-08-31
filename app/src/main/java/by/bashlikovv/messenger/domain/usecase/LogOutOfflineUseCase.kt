package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.repository.SQLiteAccountsRepository

class LogOutOfflineUseCase(private val accountsRepository: SQLiteAccountsRepository) {

    suspend fun execute() {
        accountsRepository.logout()
    }
}