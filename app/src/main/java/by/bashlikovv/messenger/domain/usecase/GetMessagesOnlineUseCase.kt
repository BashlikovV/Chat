package by.bashlikovv.messenger.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import by.bashlikovv.messenger.data.remote.model.GetMessagesResult
import by.bashlikovv.messenger.data.remote.model.GetServerMessagesResult
import by.bashlikovv.messenger.data.remote.model.ServerRoom
import by.bashlikovv.messenger.data.repository.MessagesRepository
import by.bashlikovv.messenger.domain.model.Pagination

class GetMessagesOnlineUseCase(private val messagesRepository: MessagesRepository) {

    @RequiresApi(Build.VERSION_CODES.O)
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