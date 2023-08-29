package by.bashlikovv.messenger.data.repository

import by.bashlikovv.messenger.domain.repository.IRoomsRepository
import by.bashlikovv.messenger.data.remote.OkHttpRoomsSource
import by.bashlikovv.messenger.data.remote.base.OkHttpConfig
import by.bashlikovv.messenger.data.remote.model.ServerRoom

class OkHTTPRoomsRepository(
    okHttpConfig: OkHttpConfig
) : IRoomsRepository {

    private val roomsSource = OkHttpRoomsSource(okHttpConfig)

    override suspend fun onDeleteChat(user1: String, user2: String) {
        roomsSource.deleteRoom(user1, user2)
    }

    override suspend fun onCreateChat(user1: String, user2: String) {
        roomsSource.addRoom(user1, user2)
    }

    override suspend fun getRooms(userToken: String): List<ServerRoom> {
        return roomsSource.getRooms(userToken)
    }

    override suspend fun getRoom(user1: String, user2: String): ServerRoom {
        return roomsSource.getRoom(user1, user2)
    }
}