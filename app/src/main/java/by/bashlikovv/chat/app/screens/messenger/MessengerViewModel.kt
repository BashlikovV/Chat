package by.bashlikovv.chat.app.screens.messenger

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.bashlikovv.chat.R
import by.bashlikovv.chat.Repositories
import by.bashlikovv.chat.Repositories.applicationContext
import by.bashlikovv.chat.app.model.accounts.AccountsRepository
import by.bashlikovv.chat.app.model.accounts.entities.Account
import by.bashlikovv.chat.app.screens.login.UserImage
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.struct.Message
import by.bashlikovv.chat.app.struct.Pagination
import by.bashlikovv.chat.app.struct.User
import by.bashlikovv.chat.app.utils.SecurityUtilsImpl
import by.bashlikovv.chat.sources.SourceProviderHolder
import by.bashlikovv.chat.sources.accounts.OkHttpAccountsSource
import by.bashlikovv.chat.sources.messages.OkHttpMessagesSource
import by.bashlikovv.chat.sources.rooms.OkHttpRoomsSource
import by.bashlikovv.chat.sources.structs.Room
import by.bashlikovv.chat.sources.users.OkHttpUsersSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * [MessengerViewModel] - class that contains data of [MessengerView]
 * */

class MessengerViewModel(
    private val accountsRepository: AccountsRepository
) : ViewModel() {

    private val _messengerUiState = MutableStateFlow(MessengerUiState())
    val messengerUiState = _messengerUiState.asStateFlow()

    private val _updateVisibility = MutableStateFlow(false)
    var updateVisibility = _updateVisibility.asStateFlow()

    private val sourceProvider = SourceProviderHolder().sourcesProvider

    private val usersSource = OkHttpUsersSource(sourceProvider)
    private val roomsSource = OkHttpRoomsSource(sourceProvider)
    private val messagesSource = OkHttpMessagesSource(sourceProvider)
    private val accountsSource = OkHttpAccountsSource(sourceProvider)

    private val selectedItem
        get() = _messengerUiState.value.selectedItem

    private fun applyMessengerUiState(state: MessengerUiState) {
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
     * [onActionPin] - function that pinned chat in list of [Chat]
     * */
    fun onActionPin(chat: Chat) {
        onActionCloseItems()
        val newState = _messengerUiState.value.chats.toMutableList()
        newState.remove(chat)
        newState.add(0, chat)
        _messengerUiState.update { it.copy(chats = newState) }
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
        updateSearchData(newValue)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateSearchData(newValue: String) = viewModelScope.launch(Dispatchers.IO) {
        setUpdateVisibility(true)
        val result = suspendCancellableCoroutine {
            viewModelScope.launch(Dispatchers.IO) {
                _messengerUiState.update {
                    it.copy(searchedItems = getSearchOutput(newValue))
                }
                it.resumeWith(Result.success(false))
            }
        }
        setUpdateVisibility(result)
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
                users = usersSource.getAllUsers(_messengerUiState.value.me.userToken)
            } catch (e: Exception) {
                result.add(Chat(User(userName = "Network error."), messages = listOf(Message(value = ""))))
                return result
            }
            if (input.isEmpty()) {
                users.forEach {
                    result.add(
                        Chat(
                            user = User(
                                userName = it.username, userToken = SecurityUtilsImpl().bytesToString(it.token),
                                userImage = UserImage(
                                    userImageBitmap = messagesSource.getImage(it.image.decodeToString()),
                                    userImageUri = Uri.parse(it.image.decodeToString())
                                )
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
                                        userToken = SecurityUtilsImpl().bytesToString(it.token),
                                        userImage = UserImage(
                                            userImageBitmap = messagesSource.getImage(it.image.decodeToString()),
                                            userImageUri = Uri.parse(it.image.decodeToString())
                                        )
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
    private fun applyMe(me: User) {
        _messengerUiState.update { it.copy(me = me) }
    }

    private suspend fun getUser(): User {
        val data: Account? = accountsRepository.getAccount().first()
        return User(
            userId = data?.id ?: 0,
            userName = data?.username ?: "unknown user",
            userEmail = data?.email ?: "unknown email",
            userToken = data?.token ?: "token error"
        )
    }

    private suspend fun getBookmarks(): List<Message>? {
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

    private suspend fun getRooms(): List<Room> {
        return roomsSource.getRooms(
            getUser().userToken
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getMessagesByRoom(room: Room): List<Message> {
        val result: MutableList<Message> = mutableListOf()

        try {
            val it = messagesSource.getRoomMessages(
                room = SecurityUtilsImpl().bytesToString(room.token),
                pagination = Pagination(0, 1).getRange()
            ).last()
            val user = if (room.user2.username == getUser().userName) {
                room.user1
            } else {
                room.user2
            }
            result.add(
                Message(
                    value = it.value.decodeToString(),
                    user = User(
                        userName = user.username,
                        userToken = SecurityUtilsImpl().bytesToString(user.token),
                        userEmail = user.email
                    ),
                    time = it.time,
                    from = SecurityUtilsImpl().bytesToString(user.token)
                )
            )
        } catch (e: Exception) {
            result.add(Message(value = "You do not have messages now.", time = ""))
        } finally {
            if (result.isEmpty()) {
                result.add(Message(value = "You do not have messages now.", time = ""))
            }
        }

        return result
    }

    private suspend fun getImage(imageUri: String): Bitmap {
        return messagesSource.getImage(imageUri)
    }

    private fun setUpdateVisibility(newValue: Boolean) {
        _updateVisibility.update { newValue }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadViewData() = viewModelScope.launch(Dispatchers.IO) {
        setUpdateVisibility(true)
        val result = suspendCancellableCoroutine {
            viewModelScope.launch(Dispatchers.IO) {
                updateViewData()
                it.resumeWith(Result.success(false))
            }
        }
        setUpdateVisibility(result)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateViewData() {
        applyMe(getUser())
        var chats = getBookmarks()
        if (chats.isNullOrEmpty()) {
            chats = listOf(Message(value = "You do not have bookmarks"))
        }
        val data = listOf(
            Chat(
                user = User(userName = "Bookmarks", userImage = UserImage(
                    userImageBitmap = R.drawable.bookmark.getBitmapFromImage(applicationContext)
                )),
                messages = chats
            )
        )
        applyMessengerUiState(MessengerUiState(chats = data))
        if (_messengerUiState.value.darkTheme != Repositories.accountsRepository.isDarkTheme()) {
            onThemeChange()
        }
        loadChatsFromServer()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun loadChatsFromServer() {
        var rooms: List<Room> = listOf()
        var result: List<Chat>
        try {
            rooms = getRooms()
        } catch (e: Exception) {
            result = listOf(Chat(messages = listOf(Message(value = "${e.message}")), time = ""))
            applyMessengerUiState(
                MessengerUiState(chats = _messengerUiState.value.chats + result)
            )
        }
        result = rooms.map {
            val user = if (it.user2.username == getUser().userName) {
                it.user1
            } else {
                it.user2
            }
            val messages = getMessagesByRoom(room = it)
            val image = UserImage(
                userImageBitmap = getImage(user.image.decodeToString()),
                userImageUri = Uri.parse(user.image.decodeToString())
            )
            Chat(
                user = User(
                    userName = user.username,
                    userToken = SecurityUtilsImpl().bytesToString(user.token),
                    userImage = image
                ),
                messages = messages,
                token = SecurityUtilsImpl().bytesToString(it.token)
            )
        }

        applyMessengerUiState(
            MessengerUiState(chats = _messengerUiState.value.chats + result)
        )
    }

    private fun Int.getBitmapFromImage(context: Context): Bitmap {
        val db = ContextCompat.getDrawable(context, this)
        val bit = Bitmap.createBitmap(
            db!!.intrinsicWidth, db.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bit)
        db.setBounds(0, 0, canvas.width, canvas.height)
        db.draw(canvas)

        return bit
    }

    fun updateUsername(newName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            accountsSource.updateUsername(
                token = accountsRepository.getAccount().first()?.token ?: "",
                newName = newName
            )
        }
    }
}