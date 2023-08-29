package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.domain.model.Message
import by.bashlikovv.messenger.domain.repository.IAccountsRepository
import kotlinx.coroutines.flow.Flow

class GetBookmarksOfflineUseCase(private val accountsRepository: IAccountsRepository) {

    suspend fun execute(): Flow<List<Message>?> {
        return accountsRepository.getBookmarks()
    }
}