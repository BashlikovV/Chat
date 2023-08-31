package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.repository.SQLiteAccountsRepository
import by.bashlikovv.messenger.data.local.model.Account
import kotlinx.coroutines.flow.Flow

class GetAccountOfflineUseCase(private val accountsRepository: SQLiteAccountsRepository) {

    suspend fun execute(): Flow<Account?> {
        return accountsRepository.getAccount()
    }
}