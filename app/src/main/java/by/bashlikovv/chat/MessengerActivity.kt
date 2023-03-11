package by.bashlikovv.chat

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import by.bashlikovv.chat.screens.messenger.MessengerUiState
import by.bashlikovv.chat.screens.messenger.MessengerView
import by.bashlikovv.chat.screens.messenger.MessengerViewModel
import by.bashlikovv.chat.struct.Chat
import by.bashlikovv.chat.struct.Message
import by.bashlikovv.chat.struct.User
import by.bashlikovv.chat.theme.MessengerTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MessengerActivity : ComponentActivity() {
    private lateinit var chatIntent: Intent
    private var data: List<Message>? = null

    companion object {
        const val DARK_THEME = "dark theme"
        const val CHAT = "chat"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Repositories.init(this)
        val messengerViewModel: MessengerViewModel by viewModels()
        updateViewData(messengerViewModel)
        setContent {
            val messengerUiState by messengerViewModel.messengerUiState.collectAsState()

            MessengerTheme(darkTheme = messengerUiState.darkTheme) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.primary) {
                    MessengerView {
                        chatIntent = Intent(this.applicationContext, ChatActivity::class.java)
                        chatIntent.apply {
                            putExtra(DARK_THEME, messengerUiState.darkTheme)
                            putExtra(CHAT, it)
                        }
                        startActivity(chatIntent)
                    }
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun updateViewData(messengerViewModel: MessengerViewModel) {
        GlobalScope.launch {
            messengerViewModel.applyMe(messengerViewModel.getUser())
            data = messengerViewModel.getBookmarks()
            if (data.isNullOrEmpty()) {
                data =  listOf(Message(value = "You do not have bookmarks"))
            }
            val bookmarks = listOf(
                Chat(
                    user = User(userName = "Bookmarks"),
                    messages = data!!
                )
            )
            messengerViewModel.applyMessengerUiState(MessengerUiState(chats = bookmarks))
        }
    }

    override fun onRestart() {
        val messengerViewModel: MessengerViewModel by viewModels()
        updateViewData(messengerViewModel)
        super.onRestart()
    }
}