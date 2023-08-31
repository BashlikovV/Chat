package by.bashlikovv.messenger.domain.repository

import by.bashlikovv.messenger.data.remote.model.ServerRoom

interface IRoomsRepository {

    suspend fun onDeleteChat(user1: String, user2: String)

    suspend fun onCreateChat(user1: String, user2: String)

    suspend fun getRooms(userToken: String): List<ServerRoom>

    suspend fun getRoom(user1: String, user2: String): ServerRoom
}