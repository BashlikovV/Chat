package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.repository.SQLiteAccountsRepository

class CheckDarkThemeUseCase(private val accountsRepository: SQLiteAccountsRepository) {

    suspend fun execute(): Boolean {
        return accountsRepository.isDarkTheme()
    }
}