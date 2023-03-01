package by.bashlikovv.chat

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import by.bashlikovv.chat.messenger.MessengerView
import by.bashlikovv.chat.messenger.MessengerViewModel
import by.bashlikovv.chat.model.MessengerTestData
import by.bashlikovv.chat.model.MessengerUiState
import by.bashlikovv.chat.struct.User
import by.bashlikovv.chat.theme.MessengerTheme

class MessengerActivity : ComponentActivity() {
    private lateinit var chatIntent: Intent

    companion object {
        const val DARK_THEME = "Dark theme"
        const val CHAT = "Chat"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val messengerViewModel: MessengerViewModel by viewModels()
            val messengerUiState by messengerViewModel.messengerUiState.collectAsState()

            LaunchedEffect(Unit) {
                messengerViewModel.applyMessengerUiState(MessengerUiState(MessengerTestData.testData))
                messengerViewModel.applyMe(User(userName = messengerViewModel.getUserName()))
            }

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
}