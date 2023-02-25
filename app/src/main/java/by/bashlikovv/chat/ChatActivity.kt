package by.bashlikovv.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import by.bashlikovv.chat.chat.ChatView
import by.bashlikovv.chat.chat.ChatViewModel
import by.bashlikovv.chat.struct.Chat
import by.bashlikovv.chat.theme.MessengerTheme

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val chatViewModel: ChatViewModel by viewModels()
        val darkTheme = intent.extras?.getBoolean(MessengerActivity.DARK_THEME)
        val data = intent.extras?.getParcelable<Chat>(MessengerActivity.CHAT)
        if (data != null) {
            chatViewModel.applyChatData(data)
        }

        setContent {
            val chatUiState by chatViewModel.chatUiState.collectAsState()

            MessengerTheme(darkTheme = darkTheme ?: true) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ChatView {
                        intent.apply {
                            putExtra(MessengerActivity.CHAT, chatUiState.chat)
                        }
                        onBackPressed()
                    }
                }
            }
        }
    }
}