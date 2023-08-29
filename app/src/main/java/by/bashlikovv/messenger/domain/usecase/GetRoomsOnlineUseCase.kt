package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.remote.model.ServerRoom
import by.bashlikovv.messenger.domain.repository.IRoomsRepository

class GetRoomsOnlineUseCase(private val roomsRepository: IRoomsRepository) {

    suspend fun execute(token: String): List<ServerRoom> {
        return roomsRepository.getRooms(token)
    }
}