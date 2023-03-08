package by.bashlikovv.chat.messenger

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import by.bashlikovv.chat.model.MessengerUiState
import by.bashlikovv.chat.struct.Chat
import by.bashlikovv.chat.struct.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * [MessengerViewModel] - class that contains data of [MessengerView]
 * */

class MessengerViewModel : ViewModel() {

    private val _messengerUiState = MutableStateFlow(MessengerUiState())
    val messengerUiState = _messengerUiState.asStateFlow()

    private val selectedItem
        get() = _messengerUiState.value.selectedItem

    fun applyMessengerUiState(state: MessengerUiState) {
        _messengerUiState.update { state }
    }

    /**
     * [onLongPress] - function that opening items in top bar when messenger item selected
     * */
    private fun onLongPress() {
        _messengerUiState.update { it.copy(visible = true) }
    }

    /**
     * [onActionCloseItems] - function that closing items in top bar when some action activated
     * */
    private fun onActionCloseItems() {
        _messengerUiState.update { it.copy(visible = false, selectedItem = Chat(User(userId = -1)), expanded = false) }
    }

    /**
     * [onActionRead] - function that reading message in list of [Chat], when same action called
     */
    fun onActionRead(chat: Chat) {
        onActionCloseItems()
        _messengerUiState.update { currentState ->
            currentState.copy(chats = currentState.chats.map { if (it == chat) it.copy(count = 0) else it })
        }
    }

    /**
     * [onActionDelete] - function that delete element from the list of [Chat] when same event called
     * */
    fun onActionDelete(chat: Chat) {
        onActionCloseItems()
        _messengerUiState.update { currentState ->
            currentState.copy(chats = currentState.chats.toMutableList().apply { remove(chat) })
        }
    }

    /**
     * [onActionPin] - function that pinned chat in list of [Chat]. TODO(Not implemented)
     * */
    fun onActionPin() {
        onActionCloseItems()
    }

    /**
     * [onActionMenu] - function that opens or closes drawer
     * */
    fun onActionMenu() {
        onActionCloseItems()
        onExpandModalDrawer()
    }

    /**
     * [onActionSelect] - function that changes current state of [MessengerUiState.selectedItem]
     * */
    fun onActionSelect(chat: Chat) {
        onLongPress()
        _messengerUiState.update { it.copy(selectedItem = chat, expanded = false) }
    }

    /**
     * [onExpandModalDrawer] - function that implements logic of [onActionMenu] function
     * */
    private fun onExpandModalDrawer() {
        _messengerUiState.update {
            it.copy(
                drawerState = if (it.drawerState.isClosed)
                    DrawerState(DrawerValue.Open)
                else
                    DrawerState(DrawerValue.Closed)
            )
        }
    }

    /**
     * [onActionOpenChat] - function for open chat. TODO(Not implemented)
     * */
    fun onActionOpenChat(chat: Chat) {
        onActionCloseItems()
        _messengerUiState.update { it.copy(selectedItem = chat) }
    }

    /**
     * [onThemeChange] - function that changes theme of application
     * */
    fun onThemeChange() {
        _messengerUiState.update { it.copy(darkTheme = !it.darkTheme) }
    }

    /**
     * [getTextColor] - function that return text color for item from list of [Chat]
     * */
    @Composable
    fun getTextColor(chat: Chat): Color {
        return if (chat.user.userId == selectedItem.user.userId) {
            Color.White
        } else {
            MaterialTheme.colors.primaryVariant
        }
    }

    /**
     * [getTintColor] - function that return tint color for images from list of [Chat]
     * */
    @Composable
    fun getTintColor(chat: Chat): Color {
        return if (chat.user.userId == selectedItem.user.userId) {
            MaterialTheme.colors.background
        } else {
            MaterialTheme.colors.primary
        }
    }

    /**
     * [getCountColor] - function that return count background color for item from list of [Chat]
     * */
    @Composable
    fun getCountColor(chat: Chat): Color {
        return if (chat.user.userId == selectedItem.user.userId) {
            MaterialTheme.colors.primary
        } else {
            MaterialTheme.colors.secondary
        }
    }

    /**
     * [getChatBackground] - function that return background color for item from list of [Chat]
     * */
    @Composable
    fun getChatBackground(chat: Chat): Color {
        return if (chat.user.userId == selectedItem.user.userId) {
            MaterialTheme.colors.primary
        } else {
            MaterialTheme.colors.background
        }
    }

    /**
     * [onAnimateContentClick] - function that opens and closes input field used for search. [MessengerUiState.expanded]
     * */
    fun onAnimateContentClick() {
        _messengerUiState.update { it.copy(expanded = !it.expanded) }
    }

    /**
     * [onSearchInputChange] - function that updates search input state in [MessengerUiState.searchInput]
     * */
    fun onSearchInputChange(newValue: String) {
        _messengerUiState.update { it.copy(searchInput = newValue) }
    }

    /**
     * [onSearchInputChange] - function that search concurrence between search input and list of [Chat]
     * */
    fun onSearchCalled() {
        var result = _messengerUiState.value.chats.map {
            if (it.user.userName.contains(_messengerUiState.value.searchInput)) {
                it
            } else {
                Chat(time = (-1).toString())
            }
        }
        result = result.filter { it.time != (-1).toString() }
        _messengerUiState.update { it.copy(searchedItems = result) }
    }

    /**
     * [applyMe] - function for applying current user data after registration
     * */
    fun applyMe(me: User) {
        _messengerUiState.update { it.copy(me = me) }
    }

    fun getUserName(): String {
        return FirebaseAuth.getInstance().currentUser?.displayName ?: "Unknown user"
    }
}