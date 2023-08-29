package by.bashlikovv.messenger.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore.Images.Media.getBitmap
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.LazyListState
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.bashlikovv.messenger.data.remote.model.ServerMessage
import by.bashlikovv.messenger.data.remote.model.ServerUser
import by.bashlikovv.messenger.domain.model.Chat
import by.bashlikovv.messenger.domain.model.Message
import by.bashlikovv.messenger.domain.model.PAGE_HEIGHT
import by.bashlikovv.messenger.domain.model.Pagination
import by.bashlikovv.messenger.domain.model.User
import by.bashlikovv.messenger.domain.usecase.DeleteBookmarkUseCase
import by.bashlikovv.messenger.domain.usecase.DeleteChatOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.DeleteMessageUseCase
import by.bashlikovv.messenger.domain.usecase.GetAccountOfflineUseCase
import by.bashlikovv.messenger.domain.usecase.GetMessagesOfflineUseCase
import by.bashlikovv.messenger.domain.usecase.GetMessagesOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.GetRoomUseCase
import by.bashlikovv.messenger.domain.usecase.GetUserImageOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.SendBookmarkUseCase
import by.bashlikovv.messenger.domain.usecase.SendImageUseCase
import by.bashlikovv.messenger.domain.usecase.SendMessageUseCase
import by.bashlikovv.messenger.presentation.view.chat.ChatUiState
import by.bashlikovv.messenger.presentation.view.login.UserImage
import by.bashlikovv.messenger.utils.SecurityUtilsImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.concurrent.thread

class ChatViewModel(
    private val getRoomMessagesOnlineUseCase: GetMessagesOnlineUseCase,
    private val getAccountOfflineUseCase: GetAccountOfflineUseCase,
    private val getUserImageOnlineUseCase: GetUserImageOnlineUseCase,
    private val getMessagesOfflineUseCase: GetMessagesOfflineUseCase,
    private val getMessageImageOnlineUseCase: GetUserImageOnlineUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getRoomUseCase: GetRoomUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val sendImageUseCase: SendImageUseCase,
    private val sendBookmarkUseCase: SendBookmarkUseCase,
    private val deleteBookmarkUseCase: DeleteBookmarkUseCase,
    private val deleteChatOnlineUseCase: DeleteChatOnlineUseCase,
    private val getMessagesOnlineUseCase: GetMessagesOnlineUseCase
) : ViewModel() {

    private val _chatUiState = MutableStateFlow(ChatUiState())
    val chatUiState = _chatUiState.asStateFlow()

    private val _lazyListState = MutableStateFlow(
        LazyListState(_chatUiState.value.chat.messages.size)
    )
    val lazyListState = _lazyListState.asStateFlow()

    private val _updateVisibility = MutableStateFlow(false)
    var updateVisibility = _updateVisibility.asStateFlow()

    private val _dMenuExpanded = MutableStateFlow(false)
    var dMenuExpanded = _dMenuExpanded.asStateFlow()

    private val _chatInputState = MutableStateFlow("")
    var chatInputState = _chatInputState.asStateFlow()

    private val _selectedItemsState = MutableStateFlow(mapOf(Message() to false))
    val selectedItemsState = _selectedItemsState.asStateFlow()

    private var me = ServerUser()

    private val pagination = Pagination()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val thread = thread(start = false, isDaemon = true) {
        Handler(Looper.getMainLooper()).post {
            SessionTimer().start()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    inner class SessionTimer(
        millsInFuture: Long = Long.MAX_VALUE,
        countDownInterval: Long = 2000
    ) : CountDownTimer(millsInFuture, countDownInterval) {
        override fun onTick(millisUntilFinished: Long) {
            viewModelScope.launch(Dispatchers.IO) {
                periodicUpdateWork()
            }
        }

        override fun onFinish() {
            thread.interrupt()
        }
    }

    private fun periodicUpdateWork() {
        var chatData: Chat
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val getMessagesResult = getRoomMessagesOnlineUseCase.execute(
                    _chatUiState.value.chat.token,
                    Pagination().getRange()
                )
                val  newValue = getMessagesResult.serverMessages.castListOfMessages()
                val tmp = _chatUiState.value.chat.messages.takeLast(newValue.size)
                if (tmp.map { it.value } == newValue.map { it.value }) {
                    return@launch
                }
                chatData = _chatUiState.value.chat.copy(messages = newValue)
//                StatusNotification.makeStatusNotification(
//                    message = "You have new messages from ${_chatUiState.value.chat.user.userName}",
//                    context = ,
//                    lastMessage = chatData.messages.last().value
//                )
                if (_chatUiState.value.chat.messages.map { it.value } != chatData.messages.map { it.value }) {
                    _chatUiState.update { it.copy(chat = chatData) }
                }
                _lazyListState.update { LazyListState(chatData.messages.size) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun applyChatData(chat: Chat) {
        _chatUiState.update { it.copy(chat = chat) }
        if (chat.messages.size > 1) {
            getUniqueUsers()
        }
    }

    fun applyMe(token: String) {
        me = me.copy(token = SecurityUtilsImpl().stringToBytes(token))
    }

    init {
        viewModelScope.launch {
            getAccountOfflineUseCase.execute().collectLatest { account ->
                me = me.copy(
                    token = SecurityUtilsImpl().stringToBytes(account?.token ?: "")
                )
            }
        }
    }

    suspend fun getMessagesFromDb() {
        _chatUiState.update {
            it.copy(chat = it.chat.copy(user = it.chat.user.copy(
                userImage = getUserImageOnlineUseCase.execute(
                    uri = _chatUiState.value.chat.user.userImage.userImageUri.encodedPath.toString()
                )
            )))
        }
        val chatData = getMessagesOfflineUseCase.execute(_chatUiState.value)
        applyChatData(chatData)
        _lazyListState.update { LazyListState(chatData.messages.size) }
    }

    private suspend fun List<ServerMessage>.castListOfMessages(): List<Message> {
        return this.map {
            var image: Bitmap? = null
            if (!it.image.contains("no image") && it.image.isNotEmpty()) {
                image = getMessageImageOnlineUseCase.execute(it.image).userImageBitmap
            }

            Message(
                value = it.value.decodeToString(),
                time = it.time,
                user = User(
                    userName = it.owner.username,
                    userToken = SecurityUtilsImpl().bytesToString(it.owner.token),
                    userEmail = it.owner.email
                ),
                isRead = it.isRead,
                from = it.from,
                isImage = image != null,
                imageBitmap = image ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            )
        }
    }

    private fun getUniqueUsers() {
        var secondUser = User()
        var token = ""
        _chatUiState.value.chat.messages.forEach {
            if (it.from != _chatUiState.value.chat.messages.last().from) {
                if (it.from.isNotEmpty()) {
                    secondUser = it.user
                    token = it.from
                }
            }
        }
        val firstUser = _chatUiState.value.chat.messages.last().user.copy(
            userToken = _chatUiState.value.chat.messages.last().from
        )
        secondUser = secondUser.copy(
            userToken = token
        )
        _chatUiState.update { it.copy(usersData = listOf(firstUser.copy(), secondUser)) }
    }

    fun onTextInputChange(newValue: String) {
        _chatInputState.update { newValue }
        _chatUiState.update {
            it.copy(isCanSend = newValue.isNotEmpty())
        }
    }

    private fun clearInput() {
        _chatInputState.update { "" }
        _chatUiState.update { it.copy(isCanSend = false) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onActionSend() {
        val newValue = sendMessageUseCase.execute(
            message = Message(
                value = chatInputState.value,
                user = User(
                    userId = me.id.toLong(),
                    userName = me.username,
                    userEmail = me.email,
                    userToken = SecurityUtilsImpl().bytesToString(me.token),
                    userImage = UserImage(
                        userImageUri = me.image.decodeToString().toUri(),
                        userImageUrl = me.image.decodeToString()
                    )
                ),
                time = Calendar.getInstance().time.toString(),
                isRead = true
            ),
            chatUiState = _chatUiState.value,
            me = me
        )
        clearInput()
        _chatUiState.update {
            it.copy(
                chat = _chatUiState.value.chat.copy(
                    messages = newValue
                )
            )
        }
        _lazyListState.update { LazyListState(_chatUiState.value.chat.messages.size) }
    }

    fun onActionDelete() = viewModelScope.launch(Dispatchers.IO) {
        val tmp = _chatUiState.value.chat.messages.toMutableList()
        val list = mutableListOf<ServerMessage>()
        val messages = _selectedItemsState.value.filter { it.value }.map { it.key }
        val room = getRoomUseCase.execute(
            SecurityUtilsImpl().bytesToString(me.token),
            _chatUiState.value.chat.user.userToken
        )
        messages.forEach { message ->
            tmp.remove(message)
            if (message.user.userName == "Bookmark") {
                viewModelScope.launch {
                    onDeleteBookmark(message)
                }
            } else {
                val owner = if (message.from == SecurityUtilsImpl().bytesToString(room.user1.token)) {
                    room.user1
                } else {
                    room.user2
                }
                list.add(
                    ServerMessage(
                    room = room,
                    image = if (message.isImage) message.value else "no image",
                    value = message.value.encodeToByteArray(),
                    file = "no file".encodeToByteArray(),
                    owner = owner,
                    time = message.time,
                    from = SecurityUtilsImpl().bytesToString(owner.token)
                )
                )
            }
        }
        deleteMessageUseCase.execute(list)
        _chatUiState.update {
            it.copy(chat = _chatUiState.value.chat.copy(messages = tmp))
        }
        _lazyListState.update { LazyListState(_chatUiState.value.chat.messages.size) }
    }

    fun onActionGallery(res: ManagedActivityResultLauncher<String, Uri?>) {
        res.launch("image/")
    }

    fun processText(message: String): String {
        var count = 0
        var result = ""

        for (i in message.indices step 1) {
            if (count == 30)  {
                result += if (message[i] == ' ') {
                    "\n"
                } else if (message[i].isLetter()) {
                    "-\n"
                } else {
                    "\n"
                }
                count = 0
            }

            count++
            result += message[i]
        }

        return result
    }

    fun applyImageUri(imageUri: Uri, context: Context) {
        try {
            val bitmap = getBitmap(context.contentResolver, imageUri)
            val message = Message(
                isImage = true,
                imageBitmap = bitmap,
                value = "",
                time = Calendar.getInstance().time.toString(),
                from = SecurityUtilsImpl().bytesToString(me.token)
            )
            val messages = _chatUiState.value.chat.messages.toMutableList()
            messages.add(message)
            _chatUiState.update { it.copy(chat = it.chat.copy(messages = messages)) }
            if (_chatUiState.value.chat.user.userName == "Bookmarks") {
                viewModelScope.launch {
                    onSendBookmark(message)
                }
            }
            viewModelScope.launch(Dispatchers.IO) {
                sendImageUseCase.execute(
                    bitmap,
                    _chatUiState.value.chat.token,
                    SecurityUtilsImpl().bytesToString(me.token),
                    false
                )
            }
        } catch (e: Exception) {
            Toast
                .makeText(context, "Error. Please, try again.", Toast.LENGTH_LONG)
                .show()
        }
    }

    private suspend fun onSendBookmark(bookmark: Message) {
        sendBookmarkUseCase.execute(bookmark)
    }

    fun onDMenuAction(value: Boolean) {
        _dMenuExpanded.update { value }
    }

    private suspend fun onDeleteBookmark(bookmark: Message) {
        deleteBookmarkUseCase.execute(bookmark)
    }

    fun onActionDeleteChat() {
        val user1 = _chatUiState.value.usersData.first().userToken
        val user2 = _chatUiState.value.usersData.last().userToken
        viewModelScope.launch(Dispatchers.IO) {
            deleteChatOnlineUseCase.execute(user1, user2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startWork() {
        thread.start()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun cancelWork() {
        thread.interrupt()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun onActionRefresh() {
        pagination.addTop(PAGE_HEIGHT)
        processRefresh()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun processRefresh() {
        val chatData: Chat
        try {
            val getMessagesResult = getMessagesOnlineUseCase.execute(
                _chatUiState.value.chat.token,
                pagination = pagination.getRange()
            )
            val  newValue = getMessagesResult.serverMessages.castListOfMessages() + _chatUiState.value.chat.messages
            chatData = _chatUiState.value.chat.copy(messages = newValue)
            val size = chatData.messages.size - _chatUiState.value.chat.messages.size
            _chatUiState.update { it.copy(chat = chatData) }
            _lazyListState.update { LazyListState(size) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setUpdateVisibility(newValue: Boolean) {
        _updateVisibility.update { newValue }
    }

    fun selectMessage(selectedMessage: Message) {
        val tmp = _selectedItemsState.value.toMutableMap()
        tmp.merge(selectedMessage, tmp[selectedMessage] ?: true) { _, _ ->
            tmp[selectedMessage]?.not() ?: true
        }
        _selectedItemsState.update { tmp }
    }

    fun clearSelectedMessages() {
        _selectedItemsState.update { mapOf(Message() to false) }
    }
}
