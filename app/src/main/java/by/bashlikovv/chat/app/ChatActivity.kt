package by.bashlikovv.chat.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import by.bashlikovv.chat.Repositories
import by.bashlikovv.chat.app.screens.chat.ChatView
import by.bashlikovv.chat.app.screens.chat.ChatViewModel
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.theme.MessengerTheme
import by.bashlikovv.chat.app.utils.viewModelCreator

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val chatViewModel: ChatViewModel by viewModelCreator {
            ChatViewModel()
        }
        val darkTheme = intent.extras?.getBoolean(MessengerActivity.DARK_THEME)
        val data = intent.extras?.getParcelable<Chat>(MessengerActivity.CHAT)
        if (data != null) {
            chatViewModel.applyChatData(data)
        }
        Repositories.init(this)

        setContent {
            MessengerTheme(darkTheme = darkTheme ?: true) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ChatView {
                        onBackPressed()
                    }
                }
            }
        }
    }
}