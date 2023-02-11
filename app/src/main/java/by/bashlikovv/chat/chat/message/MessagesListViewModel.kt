package by.bashlikovv.chat.chat.message

import androidx.lifecycle.ViewModel
import by.bashlikovv.chat.chat.message.item.MessageItemUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MessagesListViewModel : ViewModel() {

    private val _messageListUiState: MutableStateFlow<MessagesListUiSate> = MutableStateFlow(MessagesListUiSate())
    val messagesListUiSate: StateFlow<MessagesListUiSate> = _messageListUiState.asStateFlow()

    init {
        initMessageListUiState()
    }

    private fun initMessageListUiState() {
        _messageListUiState.update {
            MessageListTestData.tmpData
        }
    }

    private fun addMessage(newMessage: MessageItemUiState): MessagesListUiSate {
        val tmp = _messageListUiState.value.messages.plus(newMessage)
        return _messageListUiState.value.copy(
            messages = tmp
        )
    }

    fun updateChat() {
        //TODO("Logic for getting new message")
        val newMessage = MessageItemUiState()
        addMessage(newMessage)
    }

    fun onChangeInput(newValue: String) {
        _messageListUiState.update {
            it.copy(input = newValue)
        }
    }

    fun onActionSend() {
        _messageListUiState.update {
            it.copy(input = "")
        }
    }
}