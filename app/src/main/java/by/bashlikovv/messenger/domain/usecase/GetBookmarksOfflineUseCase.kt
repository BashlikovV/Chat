package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.repository.SQLiteAccountsRepository
import by.bashlikovv.messenger.domain.model.Message
import kotlinx.coroutines.flow.Flow

class GetBookmarksOfflineUseCase(private val accountsRepository: SQLiteAccountsRepository) {

    suspend fun execute(): Flow<List<Message>?> {
        return accountsRepository.getBookmarks()
    }
}