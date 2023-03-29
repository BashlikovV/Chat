package by.bashlikovv.chat.app.screens.messenger

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.bashlikovv.chat.app.model.accounts.AccountsRepository
import by.bashlikovv.chat.app.model.accounts.entities.Account
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.struct.Message
import by.bashlikovv.chat.app.struct.User
import by.bashlikovv.chat.app.utils.SecurityUtilsImpl
import by.bashlikovv.chat.sources.SourceProviderHolder
import by.bashlikovv.chat.sources.messages.OkHttpMessagesSource
import by.bashlikovv.chat.sources.rooms.OkHttpRoomsSource
import by.bashlikovv.chat.sources.structs.Room
import by.bashlikovv.chat.sources.users.OkHttpUsersSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * [MessengerViewModel] - class that contains data of [MessengerView]
 * */

class MessengerViewModel(
    private val accountsRepository: AccountsRepository
) : ViewModel() {

    private val _messengerUiState = MutableStateFlow(MessengerUiState())
    val messengerUiState = _messengerUiState.asStateFlow()

    private val sourceProvider = SourceProviderHolder().sourcesProvider

    private val usersSource = OkHttpUsersSource(sourceProvider)
    private val roomsSource = OkHttpRoomsSource(sourceProvider)
    private val messagesSource = OkHttpMessagesSource(sourceProvider)

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
        viewModelScope.launch {
            roomsSource.deleteRoom(
                user1 = getUser().userToken,
                user2 = chat.user.userToken
            )
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
     * [onActionOpenChat] - function for open chat.
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
        return if (chat == selectedItem) {
            MaterialTheme.colors.primary
        } else {
            MaterialTheme.colors.secondary
        }
    }

    /**
     * [getTintColor] - function that return tint color for images from list of [Chat]
     * */
    @Composable
    fun getTintColor(chat: Chat): Color {
        return if (chat == selectedItem) {
            MaterialTheme.colors.primary
        } else {
            MaterialTheme.colors.background
        }
    }

    /**
     * [getCountColor] - function that return count background color for item from list of [Chat]
     * */
    @Composable
    fun getCountColor(chat: Chat): Color {
        return if (chat == selectedItem) {
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
        return if (chat == selectedItem) {
            MaterialTheme.colors.background
        } else {
            MaterialTheme.colors.primary
        }
    }

    /**
     * [onSearchClick] - function that opens and closes input field used for search. [MessengerUiState.expanded]
     * */
    fun onSearchClick(newChat: Boolean) {
        _messengerUiState.update { it.copy(expanded = !it.expanded, newChat = newChat) }
    }

    /**
     * [onSearchInputChange] - function that updates search input state in [MessengerUiState.searchInput]
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    fun onSearchInputChange(newValue: String) {
        _messengerUiState.update {
            it.copy(searchInput = newValue)
        }
        viewModelScope.launch {
            _messengerUiState.update {
                it.copy(searchedItems = getSearchOutput(newValue))
            }
        }
    }

    /**
     * [getSearchOutput] - function for getting searched elements
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getSearchOutput(input: String): List<Chat> {
        var result = mutableListOf<Chat>()
        if (_messengerUiState.value.newChat) {
            val users: List<by.bashlikovv.chat.sources.structs.User>
            try {
                users = usersSource.getAllUsers()
            } catch (e: Exception) {
                result.add(Chat(User(userName = "Network error."), messages = listOf(Message(value = ""))))
                return result
            }
            if (input.isEmpty()) {
                users.forEach {
                    result.add(
                        Chat(
                            user = User(
                                userName = it.username, userToken = SecurityUtilsImpl().bytesToString(it.token)
                            ),
                            messages = listOf(Message(value = "")), time = "")
                    )
                }
            } else {
                users.forEach {
                    if (input.length <= it.username.length) {
                        val subStr = it.username.subSequence(0, input.length).toString().lowercase()
                        if (subStr == input.lowercase() && subStr != "") {
                            result.add(
                                Chat(
                                    user = User(
                                        userName = it.username,
                                        userToken = SecurityUtilsImpl().bytesToString(it.token)
                                    ),
                                    messages = listOf(Message(value = "")),
                                    time = ""
                                )
                            )
                        }
                    }
                }
            }
        } else {
            if (input.isEmpty()) {
                result = _messengerUiState.value.chats.toMutableList()
            } else {
                _messengerUiState.value.chats.forEach {
                    if (input.length <= it.user.userName.length) {
                        val subStr = it.user.userName.subSequence(0, input.length).toString().lowercase()
                        if (subStr == input.lowercase()) {
                            result.add(it)
                        }
                    }
                }
            }
        }

        return result
    }

    /**
     * [applyMe] - function for applying current user data after registration
     * */
    fun applyMe(me: User) {
        _messengerUiState.update { it.copy(me = me) }
    }

    suspend fun getUser(): User {
        val data: Account? = accountsRepository.getAccount().first()
        return User(
            userId = data?.id ?: 0,
            userName = data?.username ?: "unknown user",
            userEmail = data?.email ?: "unknown email",
            userToken = data?.token ?: "token error"
        )
    }

    suspend fun getBookmarks(): List<Message>? {
        return accountsRepository.getBookmarks().first()
    }

    fun onSignOut() {
        viewModelScope.launch {
            accountsRepository.logout()
        }
    }

    fun onAddChatClicked(value: Boolean) {
        _messengerUiState.update { it.copy(newChat = value) }
        onSearchClick(true)
    }

    fun onCreateNewChat(user: User) {
        val tmp = _messengerUiState.value.chats.toMutableList()
        tmp.add(Chat(user, messages = listOf(Message(value = "You do not have messages now."))))
        _messengerUiState.update { it.copy(chats = tmp) }
        viewModelScope.launch {
            addRoom(
                getUser().userToken,
                _messengerUiState.value.chats.last().user.userToken
            )
        }
    }

    private suspend fun addRoom(user1: String, user2: String): String {
        return roomsSource.addRoom(user1, user2)
    }

    suspend fun getRooms(): List<Room> {
        return roomsSource.getRooms(
            getUser().userToken
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getMessagesByRoom(room: Room): List<Message> {
        val result: MutableList<Message> = mutableListOf()

        try {
            val it = messagesSource.getRoomMessages(
                SecurityUtilsImpl().bytesToString(room.token)
            ).last()
            result.add(
                Message(
                    value = it.value,
                    user = User(
                        userName = it.owner.username,
                        userToken = SecurityUtilsImpl().bytesToString(it.owner.token),
                        userEmail = it.owner.email
                    ),
                    time = it.time,
                    from = SecurityUtilsImpl().bytesToString(it.owner.token)
                )
            )
        } catch (e: Exception) {
            result.add(Message(value = "You do not have messages now.", time = ""))
            Log.i("MYTAG", "catch: ${e.message}")
        } finally {
            if (result.isEmpty()) {
                Log.i("MYTAG", "Empty")
                result.add(Message(value = "You do not have messages now.", time = ""))
            }
        }

        Log.i("MYTAG", result.toString())
        return result
    }
}