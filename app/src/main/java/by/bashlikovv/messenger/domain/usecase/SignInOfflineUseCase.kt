package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.repository.SQLiteAccountsRepository

class SignInOfflineUseCase(private val accountsRepository: SQLiteAccountsRepository) {

    suspend fun execute(email: String, password: String) {
        accountsRepository.signIn(email, password)
    }
}