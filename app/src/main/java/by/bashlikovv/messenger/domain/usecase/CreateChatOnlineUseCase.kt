package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.repository.OkHTTPRoomsRepository

class CreateChatOnlineUseCase(private val roomsRepository: OkHTTPRoomsRepository) {

    suspend fun execute(user1: String, user2: String) {
        roomsRepository.onCreateChat(user1, user2)
    }
}