package by.bashlikovv.chat.navbar

import androidx.lifecycle.ViewModel
import by.bashlikovv.chat.R
import by.bashlikovv.chat.chat.list.ChatListUiState
import by.bashlikovv.chat.chat.list.ChatListViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TopNavBarViewModel : ViewModel() {

    private val _topNavBarUiState = MutableStateFlow(TopNavBarState())
    val topNavBarState = _topNavBarUiState.asStateFlow()

    fun onBarChange(image: Int, description: String) {
        _topNavBarUiState.update {
            it.copy(
                leadingIcon = image,
                description = description
            )
        }
    }

    fun onRemoveElem(elem: ChatListUiState, chatListViewModel: ChatListViewModel) {
        chatListViewModel.removeChatListItem(elem)
    }

    fun onReadElem(elem: ChatListUiState, chatListViewModel: ChatListViewModel) {
        chatListViewModel.readChatListItem(elem)
    }

    companion object {
        @JvmStatic val MENU_IMAGE = R.drawable.menu
        @JvmStatic val MENU_DESCRIPTION = "Menu"
        @JvmStatic val CLOSE_IMAGE = R.drawable.close
        @JvmStatic val CLOSE_DESCRIPTION = "Close"
    }
}