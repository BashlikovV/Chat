package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.domain.repository.IAccountsRepository

class SignInOfflineUseCase(private val accountsRepository: IAccountsRepository) {

    suspend fun execute(email: String, password: String) {
        accountsRepository.signIn(email, password)
    }
}