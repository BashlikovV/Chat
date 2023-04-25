package by.bashlikovv.chat.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import by.bashlikovv.chat.Repositories
import by.bashlikovv.chat.app.screens.chat.ChatView
import by.bashlikovv.chat.app.screens.chat.ChatViewModel
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.theme.MessengerTheme
import by.bashlikovv.chat.app.utils.viewModelCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val chatViewModel: ChatViewModel by viewModelCreator {
            ChatViewModel(Repositories.accountsRepository)
        }
        val darkTheme = intent.extras?.getBoolean(MessengerActivity.DARK_THEME)
        val data = intent.extras?.getParcelable<Chat>(MessengerActivity.CHAT)
        val token = intent.extras?.getString(MessengerActivity.TOKEN) ?: ""
        if (data != null) {
            chatViewModel.applyChatData(data)
            chatViewModel.applyMe(token)
        }
        Repositories.init(this)

        setContent {
            var updateVisibility by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            fun update() = scope.launch(Dispatchers.IO) {
                updateVisibility = true
                chatViewModel.getMessagesFromDb()
                delay(2000)
                updateVisibility = false
            }
            if (data?.user?.userName != "Bookmarks") {
                LaunchedEffect(Unit) { update() }
            }
            MessengerTheme(darkTheme = darkTheme ?: true) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ChatView { onBackPressed() }
                        if (updateVisibility) { ProgressIndicator() }
                    }
                }
            }
        }
    }

    @Composable
    private fun ProgressIndicator() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = androidx.compose.material.MaterialTheme.colors.secondary
            )
        }
    }
}