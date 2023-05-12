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
import by.bashlikovv.chat.app.model.chats.ChatRoomsRepository
import by.bashlikovv.chat.app.model.chats.RoomsRepository
import by.bashlikovv.chat.app.model.messages.ChatMessagesRepository
import by.bashlikovv.chat.app.model.messages.MessagesRepository
import by.bashlikovv.chat.app.model.users.ChatUsersRepository
import by.bashlikovv.chat.app.model.users.UsersRepository
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
    private val accountsRepository: AccountsRepository,
    private val roomsRepository: RoomsRepository = ChatRoomsRepository(),
    private val messagesRepository: MessagesRepository = ChatMessagesRepository(),
    private val usersRepository: UsersRepository = ChatUsersRepository()
) : ViewModel() {

    private val _messengerUiState = MutableStateFlow(MessengerUiState())
    val messengerUiState = _messengerUiState.asStateFlow()

    private val _updateVisibility = MutableStateFlow(false)
    var updateVisibility = _updateVisibility.asStateFlow()

    private val _drawerState = MutableStateFlow(DrawerState(DrawerValue.Closed))
    var drawerState = _drawerState.asStateFlow()

    private val _searchedItems = MutableStateFlow(listOf<Chat>())
    var searchedItems = _searchedItems.asStateFlow()

    private val _me = MutableStateFlow(User())
    var me = _me.asStateFlow()

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
        viewModelScope.launch(Dispatchers.IO) {
            messagesSource.readRoomMessages(chat.token)
        }
    }

    /**
     * [onActionDelete] - function that delete element from the list of [Chat] when same event called
     * */
    fun onActionDelete(chat: Chat) {
        onActionCloseItems()
        viewModelScope.launch(Dispatchers.IO) {
            roomsRepository.onDeleteChat(
                user1 = getUser().userToken,
                user2 = chat.user.userToken
            )
        }
        _messengerUiState.update { currentState ->
            currentState.copy(chats = currentState.chats.toMutableList().apply { remove(chat) })
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
        _drawerState.update {
            if (it.isClosed) {
                DrawerState(DrawerValue.Open)
            } else {
                DrawerState(DrawerValue.Closed)
            }
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
                _searchedItems.update { listOf() }
                getSearchOutput(newValue)
                it.resumeWith(Result.success(false))
            }
        }
        setUpdateVisibility(result)
    }

    /**
     * [getSearchOutput] - function for getting searched elements
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getSearchOutput(input: String) {
        if (_messengerUiState.value.newChat) {
            var users = listOf<by.bashlikovv.chat.sources.structs.User>()
            try {
                users = usersRepository.getUsers(_me.value.userToken)
            } catch (e: Exception) {
                _searchedItems.update {
                    listOf(Chat(
                        user = User(userName = "Network error."),
                        messages = listOf(Message(value = "")))
                    )
                }
            }
            if (input.isEmpty()) {
                users.forEach {
                    val tmp = Chat(
                        user = User(
                            userName = it.username, userToken = SecurityUtilsImpl().bytesToString(it.token),
                            userImage = usersRepository.getUserImage(it.image.decodeToString())
                        ),
                        messages = listOf(Message(value = "")), time = ""
                    )
                    _searchedItems.update { state ->
                        state + tmp
                    }
                }
            } else {
                users.forEach {
                    if (input.length <= it.username.length) {
                        val subStr = it.username.subSequence(0, input.length).toString().lowercase()
                        if (subStr == input.lowercase() && subStr != "") {
                            val tmp = Chat(
                                user = User(
                                    userName = it.username,
                                    userToken = SecurityUtilsImpl().bytesToString(it.token),
                                    userImage = usersRepository.getUserImage(it.image.decodeToString())
                                ),
                                messages = listOf(Message(value = "")),
                                time = ""
                            )
                            _searchedItems.update { state ->
                                state + tmp
                            }
                        }
                    }
                }
            }
        } else {
            if (input.isEmpty()) {
                val tmp = _messengerUiState.value.chats.toMutableList()
                _searchedItems.update { tmp }
            } else {
                _messengerUiState.value.chats.forEach {
                    if (input.length <= it.user.userName.length) {
                        val subStr = it.user.userName.subSequence(0, input.length).toString().lowercase()
                        if (subStr == input.lowercase()) {
                            _searchedItems.update { state ->
                                state + it
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * [applyMe] - function for applying current user data after registration
     * */
    private fun applyMe(me: User) {
        _me.update { me }
    }

    private suspend fun getUser(): User {
        val data: Account? = accountsRepository.getAccount().first()
        val userName = data?.username ?: _me.value.userName
        val userToken = data?.token ?: _me.value.userToken

        return _me.value.copy(
            userName = userName,
            userToken = userToken
        )
    }

    private suspend fun getStartUser(): User {
        val result: User = suspendCancellableCoroutine {
            viewModelScope.launch(Dispatchers.IO) {
                val data: Account? = accountsRepository.getAccount().first()
                val user = usersRepository.getUser(data?.token ?: "")
                val userBitmapImageUrl = user.image.decodeToString()
                val userImage = usersRepository.getUserImage(userBitmapImageUrl)
                it.resumeWith(
                    Result.success(
                        User(
                            userId = data?.id ?: 0,
                            userName = data?.username ?: "unknown user",
                            userEmail = data?.email ?: "unknown email",
                            userToken = data?.token ?: "token error",
                            userImage = userImage
                        )
                    )
                )
            }
        }
        return result
    }

    private suspend fun getBookmarks(): List<Message>? {
        return accountsRepository.getBookmarks().first()
    }

    fun onSignOut() {
        viewModelScope.launch(Dispatchers.IO) {
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
            roomsSource.addRoom(
                getUser().userToken,
                _messengerUiState.value.chats.last().user.userToken
            )
        }
    }

    private suspend fun getRooms(): List<Room> {
        return roomsRepository.getRooms(getUser().userToken)
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
        applyMe(getStartUser())
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
        try {
            rooms = getRooms()
        } catch (e: Exception) {
            val  result = listOf(Chat(messages = listOf(Message(value = "${e.message}")), time = ""))
            applyMessengerUiState(
                MessengerUiState(chats = _messengerUiState.value.chats + result)
            )
        }
        rooms.map {
            val user = if (it.user2.username == getUser().userName) {
                it.user1
            } else {
                it.user2
            }
            val tmp = messagesRepository.getMessagesByRoom(
                room = it,
                pagination = Pagination(0, 1),
                firstUserName = getUser().userName
            )
            val messages = tmp.messages
            val image = UserImage(
                userImageBitmap = getImage(user.image.decodeToString()),
                userImageUri = Uri.parse(user.image.decodeToString())
            )
            val chat = Chat(
                user = User(
                    userName = user.username,
                    userToken = SecurityUtilsImpl().bytesToString(user.token),
                    userImage = image
                ),
                messages = messages,
                token = SecurityUtilsImpl().bytesToString(it.token),
                time = messages.last().time,
                count = tmp.unreadMessageCount
            )
            applyMessengerUiState(
                MessengerUiState(chats = _messengerUiState.value.chats + listOf(chat))
            )
            chat
        }
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
            accountsRepository.updateAccountUsername(newUsername = newName)
        }
    }
}