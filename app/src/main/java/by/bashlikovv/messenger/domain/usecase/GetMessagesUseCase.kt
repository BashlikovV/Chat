package by.bashlikovv.messenger.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import by.bashlikovv.messenger.data.repository.MessagesRepository
import by.bashlikovv.messenger.domain.model.Chat
import by.bashlikovv.messenger.presentation.view.chat.ChatUiState

class GetMessagesUseCase(private val messagesRepository: MessagesRepository) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun execute(chatUiState: ChatUiState): Chat {
        return messagesRepository.getMessages(chatUiState)
    }
}