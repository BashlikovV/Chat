package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.domain.repository.IAccountsRepository
import by.bashlikovv.messenger.presentation.model.SignUpData

class SignUpOfflineUseCase(private val accountsRepository: IAccountsRepository) {

    suspend fun execute(signUpData: SignUpData, token: String) {
        accountsRepository.signUp(signUpData, token)
    }
}