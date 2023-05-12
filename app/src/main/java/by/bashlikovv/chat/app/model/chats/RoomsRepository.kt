package by.bashlikovv.chat.app.model.chats

import by.bashlikovv.chat.sources.structs.Room

interface RoomsRepository {

    suspend fun onDeleteChat(user1: String, user2: String)

    suspend fun onCreateChat(user1: String, user2: String)

    suspend fun getRooms(userToken: String): List<Room>
}