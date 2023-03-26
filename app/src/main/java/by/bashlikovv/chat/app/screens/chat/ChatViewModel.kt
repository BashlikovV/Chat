package by.bashlikovv.chat.app.screens.chat

import android.content.Context
import android.net.Uri
import android.provider.MediaStore.Images.Media.getBitmap
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import by.bashlikovv.chat.Repositories.accountsRepository
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.struct.Message
import by.bashlikovv.chat.app.struct.User
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

class ChatViewModel : ViewModel() {

    private val _chatUiState = MutableStateFlow(ChatUiState())
    val chatUiState = _chatUiState.asStateFlow()

    var messageCheapVisible by mutableStateOf(_chatUiState.value.chat.messages.map {
        false
    })
        private set

    fun applyChatData(chat: Chat) {
        _chatUiState.update { it.copy(chat = chat) }
        messageCheapVisible = chat.messages.map { false }
        getUniqueUsers(chat)
    }

    private fun getUniqueUsers(chat: Chat) {
        val firstUser = _chatUiState.value.chat.messages.first().user
        var secondUser = User()
        _chatUiState.value.chat.messages.forEach {
            if (it.user != firstUser) {
                secondUser = it.user
            }
        }
        _chatUiState.update { it.copy(usersData = listOf(firstUser, secondUser)) }

        //Test
        val testData = _chatUiState.value.chat.messages.map {
            if (it.user.userName == firstUser.userName) {
                it
            } else {
                it.copy(value = "my extended long long message message ${it.value.last()}")
            }
        }
        _chatUiState.update { it.copy(chat = chat.copy(messages = testData)) }
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

    @OptIn(DelicateCoroutinesApi::class)
    fun onActionSend() {
        val newValue = _chatUiState.value.chat.messages.toMutableList()
        newValue.add(
            Message(
                value = _chatUiState.value.textInputState,
                user = _chatUiState.value.usersData.last(),
                time = Calendar.getInstance().time.toGMTString()
                    .substringBefore(" G").substringAfter("2023 ")
                    .substringBeforeLast(":"),
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
            GlobalScope.launch {
                onSendBookmark(newValue.last())
            }
        }
        messageCheapVisible = messageCheapVisible.toMutableList().apply {
            add(false)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun onActionDelete(message: Message) {
        val tmp = _chatUiState.value.chat.messages.toMutableList()
        tmp.remove(message)
        messageCheapVisible = messageCheapVisible.toMutableList().apply {
            removeAt(getMessageIndex(message))
        }
        GlobalScope.launch {
            onDeleteBookmark(message)
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

    @OptIn(DelicateCoroutinesApi::class)
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
                GlobalScope.launch {
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
}
