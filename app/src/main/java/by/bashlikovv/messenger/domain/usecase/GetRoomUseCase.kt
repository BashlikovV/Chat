package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.remote.model.ServerRoom
import by.bashlikovv.messenger.data.repository.OkHTTPRoomsRepository

class GetRoomUseCase(private val roomsRepository: OkHTTPRoomsRepository) {

    suspend fun execute(user1: String, user2: String): ServerRoom {
        return roomsRepository.getRoom(user1, user2)
    }
}