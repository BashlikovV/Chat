package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.repository.OkHTTPRoomsRepository

class DeleteChatOnlineUseCase(private val roomsRepository: OkHTTPRoomsRepository) {

    suspend fun execute(user1: String, user2: String) {
        roomsRepository.onDeleteChat(user1, user2)
    }
}