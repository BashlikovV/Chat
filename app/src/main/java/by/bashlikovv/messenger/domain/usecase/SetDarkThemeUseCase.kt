package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.repository.SQLiteAccountsRepository

class SetDarkThemeUseCase(private val accountsRepository: SQLiteAccountsRepository) {

    suspend fun execute() {
        accountsRepository.setDarkTheme()
    }
}