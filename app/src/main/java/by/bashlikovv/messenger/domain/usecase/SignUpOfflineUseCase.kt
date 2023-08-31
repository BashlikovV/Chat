package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.repository.SQLiteAccountsRepository
import by.bashlikovv.messenger.presentation.model.SignUpData

class SignUpOfflineUseCase(private val accountsRepository: SQLiteAccountsRepository) {

    suspend fun execute(signUpData: SignUpData, token: String) {
        accountsRepository.signUp(signUpData, token)
    }
}