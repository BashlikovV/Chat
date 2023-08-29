package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.remote.model.ServerMessage
import by.bashlikovv.messenger.domain.repository.IMessagesRepository

class DeleteMessageUseCase(private val messagesRepository: IMessagesRepository) {

    suspend fun execute(serverMessage: List<ServerMessage>) {
        messagesRepository.deleteMessage(serverMessage)
    }
}