package by.bashlikovv.messenger.presentation.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.bashlikovv.messenger.R
import by.bashlikovv.messenger.data.remote.model.ServerRoom
import by.bashlikovv.messenger.domain.model.Chat
import by.bashlikovv.messenger.domain.model.Message
import by.bashlikovv.messenger.domain.model.Pagination
import by.bashlikovv.messenger.domain.model.User
import by.bashlikovv.messenger.domain.usecase.CheckDarkThemeUseCase
import by.bashlikovv.messenger.domain.usecase.CreateChatOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.DeleteChatOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.GetAccountOfflineUseCase
import by.bashlikovv.messenger.domain.usecase.GetBookmarksOfflineUseCase
import by.bashlikovv.messenger.domain.usecase.GetMessagesOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.GetRoomsOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.GetUserImageOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.GetUserOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.LogOutOfflineUseCase
import by.bashlikovv.messenger.domain.usecase.ReadRoomMessagesOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.SetDarkThemeUseCase
import by.bashlikovv.messenger.domain.usecase.UpdateUsernameUseCase
import by.bashlikovv.messenger.presentation.view.messenger.MessengerUiState
import by.bashlikovv.messenger.utils.SecurityUtilsImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Date

/**
 * [MessengerViewModel] - class that contains data of [MessengerView]
 * */

class MessengerViewModel(
    context: Context,
    private val readRoomMessagesOnlineUseCase: ReadRoomMessagesOnlineUseCase,
    private val deleteChatOnlineUseCase: DeleteChatOnlineUseCase,
    private val setDarkThemeUseCase: SetDarkThemeUseCase,
    private val getAccountOfflineUseCase: GetAccountOfflineUseCase,
    private val getUserOnlineUseCase: GetUserOnlineUseCase,
    private val logOutOfflineUseCase: LogOutOfflineUseCase,
    private val getUserImageOnlineUseCase: GetUserImageOnlineUseCase,
    private val createChatOnlineUseCase: CreateChatOnlineUseCase,
    private val checkDarkThemeUseCase: CheckDarkThemeUseCase,
    private val getBookmarksOfflineUseCase: GetBookmarksOfflineUseCase,
    private val getRoomsOnlineUseCase: GetRoomsOnlineUseCase,
    private val getMessagesOnlineUseCase: GetMessagesOnlineUseCase,
    private val updateUsernameUseCase: UpdateUsernameUseCase
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

    private val bookmarkBitmap by lazy {
        val rId = R.drawable.bookmark
        ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.resources.getResourcePackageName(rId) + '/' +
                context.resources.getResourceTypeName(rId) + '/' +
                context.resources.getResourceEntryName(rId)
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
        viewModelScope.launch(Dispatchers.IO) {
            readRoomMessagesOnlineUseCase.execute(chat.token)
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
            deleteChatOnlineUseCase.execute(
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
            setDarkThemeUseCase.execute()
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
        var userName = ""
        var userToken = ""
        getAccountOfflineUseCase.execute().collectLatest { data ->
            userName = data?.username ?: _me.value.userName
            userToken = data?.token ?: _me.value.userToken
        }

        return _me.value.copy(
            userName = userName,
            userToken = userToken
        )
    }

    private suspend fun getStartUser(): User {
        val result: User = suspendCancellableCoroutine { continuation ->
            viewModelScope.launch(Dispatchers.IO) {
                continuation.invokeOnCancellation { cancel() }

                getAccountOfflineUseCase.execute().collectLatest { data ->
                    val user = getUserOnlineUseCase.execute(data?.token ?: "")
                    val userBitmapImageUrl = user.image.decodeToString()
                    continuation.resumeWith(
                        Result.success(
                            User(
                                userId = data?.id ?: 0,
                                userName = data?.username ?: "unknown user",
                                userEmail = data?.email ?: "unknown email",
                                userToken = data?.token ?: "token error",
                                userImage = userBitmapImageUrl
                            )
                        )
                    )
                }
            }
        }
        return result
    }

    fun onSignOut() {
        viewModelScope.launch(Dispatchers.IO) {
            logOutOfflineUseCase.execute()
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
            createChatOnlineUseCase.execute(
                getUser().userToken,
                _messengerUiState.value.chats.last().user.userToken
            )
        }
    }

    private suspend fun getImage(imageUri: String): String {
        return getUserImageOnlineUseCase.execute(imageUri)
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
        if (_messengerUiState.value.darkTheme != checkDarkThemeUseCase.execute()) {
            _messengerUiState.update { it.copy(darkTheme = !it.darkTheme) }
        }
        _me.update { getStartUser() }
        getBookmarksOfflineUseCase.execute().collectLatest { chats ->
            var tmp = chats
            if (tmp.isNullOrEmpty()) {
                tmp = listOf(Message(value = "You do not have bookmarks"))
            }
            val data = listOf(
                Chat(
                    user = User(
                        userName = "Bookmarks", userImage = bookmarkBitmap
                    ),
                    messages = tmp
                )
            )
            _messengerUiState.update { it.copy(chats = data) }
        }
        loadChatsFromServer()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun loadChatsFromServer() {
        var serverRooms: List<ServerRoom> = listOf()
        try {
            serverRooms = getRoomsOnlineUseCase.execute(getUser().userToken)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun ServerRoom.castServerRoom(): Chat {
        val user = if (this.user2.username == getUser().userName) {
            this.user1
        } else {
            this.user2
        }
        val tmp = getMessagesOnlineUseCase.execute(
            serverRoom = this,
            pagination = Pagination(0, 1),
            firstUserName = getUser().userName
        )
        val messages = tmp.messages
        val time = try {
            Date(user.createdAt.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            Date(300000)
        }
        return Chat(
            user = User(
                userName = user.username,
                userToken = SecurityUtilsImpl().bytesToString(user.token),
                userImage = user.image.decodeToString(),
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
            getAccountOfflineUseCase.execute().collectLatest { account ->
                updateUsernameUseCase.execute(
                    token = account?.token ?: "",
                    name = newName
                )
            }
        }
    }
}