package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.domain.repository.IAccountsRepository

class LogOutOfflineUseCase(private val accountsRepository: IAccountsRepository) {

    suspend fun execute() {
        accountsRepository.logout()
    }
}