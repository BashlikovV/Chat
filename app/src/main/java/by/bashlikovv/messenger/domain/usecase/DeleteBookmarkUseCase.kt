package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.domain.model.Message
import by.bashlikovv.messenger.domain.repository.IMessagesRepository

class DeleteBookmarkUseCase(private val messagesRepository: IMessagesRepository) {

    suspend fun execute(bookmark: Message) {
        messagesRepository.onDeleteBookmark(bookmark)
    }
}