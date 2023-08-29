package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.domain.repository.IAccountsRepository

class CheckSignedInUseCase(private val accountsRepository: IAccountsRepository) {

    suspend fun execute(): Boolean {
        return accountsRepository.isSignedIn()
    }
}