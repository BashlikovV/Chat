package by.bashlikovv.chat.screens.chat

import android.content.Context
import android.net.Uri
import android.provider.MediaStore.Images.Media.getBitmap
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.lifecycle.ViewModel
import by.bashlikovv.chat.model.ChatUiState
import by.bashlikovv.chat.struct.Chat
import by.bashlikovv.chat.struct.Message
import by.bashlikovv.chat.struct.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.*

class ChatViewModel : ViewModel() {

    private val _chatUiState = MutableStateFlow(ChatUiState())
    val chatUiState = _chatUiState.asStateFlow()

    fun applyChatData(chat: Chat) {
        _chatUiState.update { it.copy(chat = chat) }
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
            if (count == 26)  {
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
        } catch (e: Exception) {
            Toast
                .makeText(context, "Error. Please, try again.", Toast.LENGTH_LONG)
                .show()
        }
    }
}
