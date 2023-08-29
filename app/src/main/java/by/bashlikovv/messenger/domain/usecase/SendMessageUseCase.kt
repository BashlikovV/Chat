package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.remote.model.ServerUser
import by.bashlikovv.messenger.domain.model.Message
import by.bashlikovv.messenger.domain.repository.IMessagesRepository
import by.bashlikovv.messenger.presentation.view.chat.ChatUiState

class SendMessageUseCase(private val messagesRepository: IMessagesRepository) {

    fun execute(
        message: Message,
        chatUiState: ChatUiState,
        me: ServerUser
    ): List<Message> {
        return messagesRepository.onSend(message, chatUiState, me)
    }
}