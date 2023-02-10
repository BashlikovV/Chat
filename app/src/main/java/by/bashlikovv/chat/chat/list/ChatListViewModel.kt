package by.bashlikovv.chat.chat.list

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChatListViewModel : ViewModel() {

    private val _chatListUiState = MutableStateFlow(listOf(ChatListUiState()))
    val chatListUiState: StateFlow<List<ChatListUiState>> = _chatListUiState.asStateFlow()

    init {
        initChatListUiState()
    }

    private fun initChatListUiState() {
        _chatListUiState.update {
            ChatListUiTestData.chatListTestData
        }
    }

    fun addChatListItem(newItem: ChatListUiState) {
        _chatListUiState.update { currentState ->
            currentState.plus(newItem)
        }
    }

    fun removeChatListItem(item: ChatListUiState) {
        _chatListUiState.update {
            removeListItem(item)
        }
    }
    
    private fun removeListItem(item: ChatListUiState): List<ChatListUiState> {
        return _chatListUiState.value.filter { it.name != item.name }
    }

    fun onChatListItemClicked(item: ChatListUiState) = item
}