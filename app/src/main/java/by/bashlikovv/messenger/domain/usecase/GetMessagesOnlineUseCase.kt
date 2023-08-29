package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.data.remote.model.GetMessagesResult
import by.bashlikovv.messenger.data.remote.model.GetServerMessagesResult
import by.bashlikovv.messenger.data.remote.model.ServerRoom
import by.bashlikovv.messenger.domain.model.Pagination
import by.bashlikovv.messenger.domain.repository.IMessagesRepository

class GetMessagesOnlineUseCase(private val messagesRepository: IMessagesRepository) {

    suspend fun execute(
        serverRoom: ServerRoom,
        pagination: Pagination,
        firstUserName: String
    ): GetMessagesResult {
        return messagesRepository.getMessagesByRoom(
            serverRoom, pagination, firstUserName
        )
    }

    suspend fun execute(
        room: String,
        pagination: IntRange
    ): GetServerMessagesResult {
        return messagesRepository.getRoomMessages(
            room, pagination
        )
    }
}