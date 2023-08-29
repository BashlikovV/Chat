package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.domain.model.Chat
import by.bashlikovv.messenger.domain.repository.IMessagesRepository
import by.bashlikovv.messenger.presentation.view.chat.ChatUiState

class GetMessagesOfflineUseCase(private val messagesRepository: IMessagesRepository) {

    suspend fun execute(chatUiState: ChatUiState): Chat {
        return messagesRepository.getMessagesFromDb(chatUiState)
    }
}