package by.bashlikovv.chat.app.screens.messenger

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.bashlikovv.chat.R
import by.bashlikovv.chat.Repositories
import by.bashlikovv.chat.Repositories.applicationContext
import by.bashlikovv.chat.app.model.accounts.AccountsRepository
import by.bashlikovv.chat.app.model.accounts.entities.Account
import by.bashlikovv.chat.app.model.chats.OkHTTPRoomsRepository
import by.bashlikovv.chat.app.model.chats.RoomsRepository
import by.bashlikovv.chat.app.model.messages.MessagesRepository
import by.bashlikovv.chat.app.model.messages.OkHTTPMessagesRepository
import by.bashlikovv.chat.app.model.users.OkHTTPUsersRepository
import by.bashlikovv.chat.app.model.users.UsersRepository
import by.bashlikovv.chat.app.screens.login.UserImage
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.struct.Message
import by.bashlikovv.chat.app.struct.Pagination
import by.bashlikovv.chat.app.struct.User
import by.bashlikovv.chat.app.utils.SecurityUtilsImpl
import by.bashlikovv.chat.sources.structs.ServerRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Date

/**
 * [MessengerViewModel] - class that contains data of [MessengerView]
 * */

class MessengerViewModel(
    private val accountsRepository: AccountsRepository
) : ViewModel() {

    private val roomsRepository: RoomsRepository = OkHTTPRoomsRepository()
    private val messagesRepository: MessagesRepository = OkHTTPMessagesRepository()
    private val usersRepository: UsersRepository = OkHTTPUsersRepository()

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
        viewModelScope.launch(Dispatchers.IO) {
            messagesRepository.readRoomMessages(chat.token)
        }
        _messengerUiState.update { currentState ->
            currentState.copy(chats = currentState.chats.map { if (it == chat) it.copy(count = 0) else it })
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
        _messengerUiState.update { it.copy(visible = true, selectedItem = chat, expanded = false) }
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
        viewModelScope.launch(Dispatchers.IO) {
            accountsRepository.setDarkTheme()
        }
    }

    /**
     * [onSearchInputChange] - function that updates search input state in [MessengerUiState.searchInput]
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    fun onSearchInputChange(newValue: String) {
        _messengerUiState.update {
            it.copy(searchInput = newValue)
        }
        updateSearchData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateSearchData() {
        val input = _messengerUiState.value.searchInput
        _searchedItems.update { listOf() }
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
        val result: User = suspendCancellableCoroutine { continuation ->
            viewModelScope.launch(Dispatchers.IO) {
                continuation.invokeOnCancellation { cancel() }

                val data: Account? = accountsRepository.getAccount().first()
                val user = usersRepository.getUser(data?.token ?: "")
                val userBitmapImageUrl = user.image.decodeToString()
                val userImage = usersRepository.getUserImage(userBitmapImageUrl)
                continuation.resumeWith(
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

    fun onSignOut() {
        viewModelScope.launch(Dispatchers.IO) {
            accountsRepository.logout()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onAddChatClicked(value: Boolean) {
        _messengerUiState.update {
            it.copy(newChat = value, expanded = !it.expanded)
        }
        onSearchInputChange("")
    }

    fun onCreateNewChat(user: User) {
        val tmp = _messengerUiState.value.chats.toMutableList()
        tmp.add(Chat(user, messages = listOf(Message(value = "You do not have messages now."))))
        _messengerUiState.update { it.copy(chats = tmp, newChat = false) }
        viewModelScope.launch(Dispatchers.IO) {
            roomsRepository.onCreateChat(
                getUser().userToken,
                _messengerUiState.value.chats.last().user.userToken
            )
        }
    }

    private suspend fun getImage(imageUri: String): Bitmap {
        return messagesRepository.getImage(imageUri).userImageBitmap
    }

    fun setUpdateVisibility(newValue: Boolean) {
        _updateVisibility.update { newValue }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadViewData() = viewModelScope.launch(Dispatchers.IO) {
        setUpdateVisibility(true)
        val result = suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation { cancel() }

            viewModelScope.launch(Dispatchers.IO) {
                updateViewData()
                continuation.resumeWith(Result.success(false))
            }
        }
        setUpdateVisibility(result)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateViewData() {
        if (_messengerUiState.value.darkTheme != Repositories.accountsRepository.isDarkTheme()) {
            _messengerUiState.update { it.copy(darkTheme = !it.darkTheme) }
        }
        _me.update { getStartUser() }
        var chats = accountsRepository.getBookmarks().first()
        if (chats.isNullOrEmpty()) {
            chats = listOf(Message(value = "You do not have bookmarks"))
        }
        val data = listOf(
            Chat(user = User(userName = "Bookmarks", userImage = UserImage(
                    userImageBitmap = R.drawable.bookmark.getBitmapFromImage(applicationContext)
                )),
                messages = chats
            )
        )
        _messengerUiState.update { it.copy(chats = data) }
        loadChatsFromServer()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun loadChatsFromServer() {
        var serverRooms: List<ServerRoom> = listOf()
        try {
            serverRooms = roomsRepository.getRooms(getUser().userToken)
        } catch (e: Exception) {
            val  result = listOf(Chat(messages = listOf(Message(value = "${e.message}")), time = ""))
            _messengerUiState.update {
                it.copy(chats = it.chats + result)
            }
        }
        serverRooms.map { serverRoom ->
            _messengerUiState.update {
                it.copy(chats = it.chats + listOf(serverRoom.castServerRoom()))
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private suspend fun ServerRoom.castServerRoom(): Chat {
        val user = if (this.user2.username == getUser().userName) {
            this.user1
        } else {
            this.user2
        }
        val tmp = messagesRepository.getMessagesByRoom(
            serverRoom = this,
            pagination = Pagination(0, 1),
            firstUserName = getUser().userName
        )
        val messages = tmp.messages
        val image = UserImage(
            userImageBitmap = getImage(user.image.decodeToString()),
            userImageUri = Uri.parse(user.image.decodeToString())
        )
        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy")
        val time = try {
            sdf.parse(user.createdAt)
        } catch (e: Exception) {
            Date(0)
        }
        return Chat(
            user = User(
                userName = user.username,
                userToken = SecurityUtilsImpl().bytesToString(user.token),
                userImage = image,
                lastConnectionTime = time
            ),
            messages = messages,
            token = SecurityUtilsImpl().bytesToString(this.token),
            time = messages.last().time,
            count = tmp.unreadMessageCount
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
            usersRepository.updateUsername(
                token = accountsRepository.getAccount().first()?.token ?: "",
                newName = newName
            )
            accountsRepository.updateAccountUsername(newUsername = newName)
        }
    }
}