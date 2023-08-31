package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.remote.model.ServerRoom
import by.bashlikovv.messenger.data.repository.OkHTTPRoomsRepository

class GetRoomsOnlineUseCase(private val roomsRepository: OkHTTPRoomsRepository) {

    suspend fun execute(token: String): List<ServerRoom> {
        return roomsRepository.getRooms(token)
    }
}