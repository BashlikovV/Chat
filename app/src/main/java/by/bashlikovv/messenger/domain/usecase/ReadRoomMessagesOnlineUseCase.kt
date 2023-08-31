package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.repository.MessagesRepository

class ReadRoomMessagesOnlineUseCase(private val messagesRepository: MessagesRepository) {

    suspend fun execute(token: String) {
        messagesRepository.readRoomMessages(token)
    }
}