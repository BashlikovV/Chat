package by.bashlikovv.chat.app.model.chats

import by.bashlikovv.chat.sources.SourceProviderHolder
import by.bashlikovv.chat.sources.rooms.OkHttpRoomsSource
import by.bashlikovv.chat.sources.structs.Room

class ChatRoomsRepository : RoomsRepository {

    private val sourceProvider = SourceProviderHolder().sourcesProvider

    private val roomsSource = OkHttpRoomsSource(sourceProvider)

    override suspend fun onDeleteChat(user1: String, user2: String) {
        roomsSource.deleteRoom(user1, user2)
    }

    override suspend fun onCreateChat(user1: String, user2: String) {
        roomsSource.addRoom(user1, user2)
    }

    override suspend fun getRooms(userToken: String): List<Room> {
        return roomsSource.getRooms(userToken)
    }

    override suspend fun getRoom(user1: String, user2: String): Room {
        return roomsSource.getRoom(user1, user2)
    }
}