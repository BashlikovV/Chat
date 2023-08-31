package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.remote.model.ServerMessage
import by.bashlikovv.messenger.data.repository.MessagesRepository

class DeleteMessageUseCase(private val messagesRepository: MessagesRepository) {

    suspend fun execute(serverMessage: List<ServerMessage>) {
        messagesRepository.deleteMessage(serverMessage)
    }
}