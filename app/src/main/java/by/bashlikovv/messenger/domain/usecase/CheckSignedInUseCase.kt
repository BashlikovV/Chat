package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.repository.SQLiteAccountsRepository

class CheckSignedInUseCase(private val accountsRepository: SQLiteAccountsRepository) {

    suspend fun execute(): Boolean {
        return accountsRepository.isSignedIn()
    }
}