package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.domain.repository.IMessagesRepository

class ReadRoomMessagesOnlineUseCase(private val messagesRepository: IMessagesRepository) {

    suspend fun execute(token: String) {
        messagesRepository.readRoomMessages(token)
    }
}