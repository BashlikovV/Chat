package by.bashlikovv.chat.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import by.bashlikovv.chat.model.ChatUiState
import by.bashlikovv.chat.struct.Chat
import by.bashlikovv.chat.struct.Message
import by.bashlikovv.chat.struct.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChatViewModel : ViewModel() {

    private val _chatUiState = MutableStateFlow(ChatUiState())
    val chatUiState = _chatUiState.asStateFlow()

    fun applyChatData(chat: Chat) {
        _chatUiState.update { it.copy(chat = chat) }
        getUniqueUsers(chat)
    }

    private fun getUniqueUsers(chat: Chat) {
        val firstUser = _chatUiState.value.chat.messages.first().user
        var secondUser = User()
        _chatUiState.value.chat.messages.forEach {
            if (it.user != firstUser) {
                secondUser = it.user
            }
        }
        _chatUiState.update { it.copy(usersData = listOf(firstUser, secondUser)) }

        //Test
        val testData = _chatUiState.value.chat.messages.map {
            if (it.user.userName == firstUser.userName) {
                it
            } else {
                it.copy(value = "my extended long long message message ${it.value.last()}")
            }
        }
        _chatUiState.update { it.copy(chat.copy(messages = testData)) }
    }

    fun onTextInputChange(newValue: String) {
        _chatUiState.update { it.copy(textInputState = newValue) }
    }

    private fun clearInput() {
        _chatUiState.update { it.copy(textInputState = "") }
    }

    suspend fun onActionSend() {
        Log.i("MYTAG", "image: ${_chatUiState.value.textInputState}")
        val newValue = _chatUiState.value.chat.messages.toMutableList().apply {
            add(
                Message(
                    value = _chatUiState.value.textInputState,
                    user = _chatUiState.value.usersData.last(),
                    time = "",
                    isRead = true
                )
            )
        }
        _chatUiState.update {
            it.copy(
                chat = _chatUiState.value.chat.copy(
                    messages = newValue
                )
            )
        }
        delay(1000)
        clearInput()
    }
}