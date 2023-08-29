package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.domain.repository.IMessagesRepository
import by.bashlikovv.messenger.domain.repository.IRoomsRepository

class DeleteChatOnlineUseCase(private val roomsRepository: IRoomsRepository) {

    suspend fun execute(user1: String, user2: String) {
        roomsRepository.onDeleteChat(user1, user2)
    }
}