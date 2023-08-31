package by.bashlikovv.messenger.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import by.bashlikovv.messenger.data.remote.model.ServerUser
import by.bashlikovv.messenger.data.repository.MessagesRepository
import by.bashlikovv.messenger.domain.model.Message
import by.bashlikovv.messenger.presentation.view.chat.ChatUiState

class SendMessageUseCase(private val messagesRepository: MessagesRepository) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun execute(
        message: Message,
        chatUiState: ChatUiState,
        me: ServerUser
    ): List<Message> {
        return messagesRepository.sendMessage(message, chatUiState, me)
    }
}