package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.domain.repository.IRoomsRepository

class CreateChatOnlineUseCase(private val roomsRepository: IRoomsRepository) {

    suspend fun execute(user1: String, user2: String) {
        roomsRepository.onCreateChat(user1, user2)
    }
}