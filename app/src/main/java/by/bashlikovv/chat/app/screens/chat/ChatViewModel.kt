package by.bashlikovv.chat.app.screens.chat

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore.Images.Media.getBitmap
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.bashlikovv.chat.Repositories.accountsRepository
import by.bashlikovv.chat.app.model.accounts.AccountsRepository
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.struct.Message
import by.bashlikovv.chat.app.struct.User
import by.bashlikovv.chat.app.utils.SecurityUtilsImpl
import by.bashlikovv.chat.sources.SourceProviderHolder
import by.bashlikovv.chat.sources.messages.OkHttpMessagesSource
import by.bashlikovv.chat.sources.rooms.OkHttpRoomsSource
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class ChatViewModel(
    accountsRepository: AccountsRepository
) : ViewModel() {

    private val _chatUiState = MutableStateFlow(ChatUiState())
    val chatUiState = _chatUiState.asStateFlow()

    var messageCheapVisible by mutableStateOf(_chatUiState.value.chat.messages.map {
        false
    })
        private set

    private val sourceProvider = SourceProviderHolder().sourcesProvider

    private val roomsSource = OkHttpRoomsSource(sourceProvider)
    private val messagesSource = OkHttpMessagesSource(sourceProvider)

    private var me = by.bashlikovv.chat.sources.structs.User()

    fun applyChatData(chat: Chat) {
        _chatUiState.update { it.copy(chat = chat) }
        messageCheapVisible = chat.messages.map { false }
        getUniqueUsers()
    }

    fun applyMe(token: String) {
        me = me.copy(token = SecurityUtilsImpl().stringToBytes(token))
    }

    init {
        viewModelScope.launch {
            me = me.copy(
                token = SecurityUtilsImpl().stringToBytes(accountsRepository.getAccount().first()?.token ?: "")
            )
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getMessagesFromDb() {
        var chatData: Chat
        GlobalScope.launch {
            try {
                val messages = messagesSource.getRoomMessages(
                    _chatUiState.value.chat.token
                )
                var size = messages.size
                val  newValue = _chatUiState.value.chat.messages + messages.map {
                    Message(
                        value = it.value.decodeToString(),
                        time = it.time,
                        user = User(
                            userName = it.owner.username,
                            userToken = SecurityUtilsImpl().bytesToString(it.owner.token),
                            userEmail = it.owner.email
                        ),
                        isRead = false,
                        from = it.from
                    )
                }
                chatData = _chatUiState.value.chat.copy(messages = _chatUiState.value.chat.messages + newValue)
                applyChatData(chatData)

                size = _chatUiState.value.chat.messages.size - size
                for (i in 0 until size) {
                    messageCheapVisible.toMutableList().add(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
        _chatUiState.update {
            it.copy(
                textInputState = newValue,
                isCanSend = newValue.isNotEmpty()
            )
        }
    }

    private fun clearInput() {
        _chatUiState.update { it.copy(textInputState = "", isCanSend = false) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onActionSend() {
        val newValue = _chatUiState.value.chat.messages.toMutableList()
        newValue.add(
            Message(
                value = _chatUiState.value.textInputState,
                user = _chatUiState.value.usersData.last(),
                time = Calendar.getInstance().time.toString(),
                isRead = true
            )
        )
        clearInput()
        _chatUiState.update {
            it.copy(
                chat = _chatUiState.value.chat.copy(
                    messages = newValue
                )
            )
        }
        if (_chatUiState.value.chat.user.userName == "Bookmarks") {
            viewModelScope.launch {
                onSendBookmark(newValue.last())
            }
        } else {
            viewModelScope.launch {
                val room = roomsSource.getRoom(
                    SecurityUtilsImpl().bytesToString(me.token),
                    _chatUiState.value.chat.user.userToken
                )
                messagesSource.sendMessage(
                    by.bashlikovv.chat.sources.structs.Message(
                        room = room,
                        value = _chatUiState.value.chat.messages.last().value.encodeToByteArray(),
                        owner = me,
                        image = "",
                        file = "".encodeToByteArray()
                    ),
                    SecurityUtilsImpl().bytesToString(me.token)
                )
            }
        }
        messageCheapVisible = messageCheapVisible.toMutableList().apply {
            add(false)
        }
    }

    fun onActionDelete(message: Message) {
        val tmp = _chatUiState.value.chat.messages.toMutableList()
        tmp.remove(message)
        messageCheapVisible = messageCheapVisible.toMutableList().apply {
            removeAt(getMessageIndex(message))
        }
        if (message.user.userName == "Bookmark") {
            viewModelScope.launch {
                onDeleteBookmark(message)
            }
        } else {
            viewModelScope.launch {
                val room = roomsSource.getRoom(
                    _chatUiState.value.usersData.first().userToken,
                    _chatUiState.value.usersData.last().userToken
                )
                val owner = if (message.from == SecurityUtilsImpl().bytesToString(room.user1.token)) {
                    room.user1
                } else {
                    room.user2
                }
                messagesSource.deleteMessage(by.bashlikovv.chat.sources.structs.Message(
                    room = room,
                    image = "",
                    value = message.value.encodeToByteArray(),
                    file = "".encodeToByteArray(),
                    owner = owner,
                    time = message.time,
                    from = SecurityUtilsImpl().bytesToString(owner.token)
                ))
            }
        }
        _chatUiState.update {
            it.copy(chat = _chatUiState.value.chat.copy(messages = tmp))
        }
    }

    fun onActionGallery(res: ManagedActivityResultLauncher<String, Uri?>) {
        res.launch("image/")
    }

    fun onActionItemClicked(message: Message) {
        _chatUiState.update { it.copy(selectedMessage = message) }
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
                imageBitmap = bitmap
            )
            val messages = _chatUiState.value.chat.messages.toMutableList()
            messages.add(message)
            _chatUiState.update { it.copy(chat = it.chat.copy(messages = messages)) }
            if (_chatUiState.value.chat.user.userName == "Bookmarks") {
                viewModelScope.launch {
                    onSendBookmark(message)
                }
            }
        } catch (e: Exception) {
            Toast
                .makeText(context, "Error. Please, try again.", Toast.LENGTH_LONG)
                .show()
        }
    }

    private suspend fun onSendBookmark(bookmark: Message) {
        accountsRepository.addBookmark(bookmark = bookmark)
    }

    fun onDMenuAction(value: Boolean) {
        _chatUiState.update { it.copy(dMenuExpanded = value) }
    }

    private suspend fun onDeleteBookmark(bookmark: Message) {
        accountsRepository.deleteBookmark(bookmark)
    }

    fun onCheapItemClicked(message: Message, value: Boolean) {
        val tmp = messageCheapVisible.toMutableList()
        tmp[getMessageIndex(message)] = value
        messageCheapVisible = tmp
    }

    fun getMessageIndex(message: Message): Int = _chatUiState.value.chat.messages.indexOf(message)

    fun onActionDeleteChat() {
        val user1 = _chatUiState.value.usersData.first().userToken
        val user2 = _chatUiState.value.usersData.last().userToken
        viewModelScope.launch {
            roomsSource.deleteRoom(user1, user2)
        }
    }
}
