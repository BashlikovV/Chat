package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.domain.repository.IAccountsRepository
import by.bashlikovv.messenger.presentation.model.Account
import kotlinx.coroutines.flow.Flow

class GetAccountOfflineUseCase(private val accountsRepository: IAccountsRepository) {

    suspend fun execute(): Flow<Account?> {
        return accountsRepository.getAccount()
    }
}