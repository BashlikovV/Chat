package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.repository.MessagesRepository
import by.bashlikovv.messenger.domain.model.Message

class SendBookmarkUseCase(private val messagesRepository: MessagesRepository) {

    suspend fun execute(bookmark: Message) {
        messagesRepository.onSendBookmark(bookmark)
    }
}