package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.domain.repository.IAccountsRepository

class SetDarkThemeUseCase(private val accountsRepository: IAccountsRepository) {

    suspend fun execute() {
        accountsRepository.setDarkTheme()
    }
}