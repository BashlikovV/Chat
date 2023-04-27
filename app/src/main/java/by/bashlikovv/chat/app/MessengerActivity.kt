package by.bashlikovv.chat.app

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import by.bashlikovv.chat.Repositories
import by.bashlikovv.chat.app.screens.messenger.MessengerUiState
import by.bashlikovv.chat.app.screens.messenger.MessengerView
import by.bashlikovv.chat.app.screens.messenger.MessengerViewModel
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.struct.Message
import by.bashlikovv.chat.app.struct.User
import by.bashlikovv.chat.app.theme.MessengerTheme
import by.bashlikovv.chat.app.utils.viewModelCreator
import kotlinx.coroutines.launch

class MessengerActivity : ComponentActivity() {
    private lateinit var chatIntent: Intent
    private val messengerViewModel: MessengerViewModel by viewModelCreator {
        MessengerViewModel(Repositories.accountsRepository)
    }

    companion object {
        const val DARK_THEME = "dark theme"
        const val CHAT = "chat"
        const val TOKEN = "token"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Repositories.init(this)
        setContent {
            val updateVisibility by messengerViewModel.updateVisibility.collectAsState()
            LaunchedEffect(Unit) { messengerViewModel.loadViewData() }

            val messengerUiState by messengerViewModel.messengerUiState.collectAsState()
            MessengerTheme(darkTheme = messengerUiState.darkTheme) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.primary) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        MessengerView {
                            startActivity(onOpenChat(messengerUiState, it))
                        }
                        if (updateVisibility) { ProgressIndicator() }
                    }
                }
            }
        }
    }

    private fun onOpenChat(messengerUiState: MessengerUiState, it: Chat): Intent {
        chatIntent = Intent(applicationContext, ChatActivity::class.java)
        if (messengerUiState.newChat) {
            messengerViewModel.onCreateNewChat(User(userToken = it.user.userToken))
        }
        chatIntent.apply {
            putExtra(DARK_THEME, messengerUiState.darkTheme)
            var chat = if (messengerUiState.newChat) {
                messengerViewModel.messengerUiState.value.chats.last()
            } else {
                it
            }
            if (chat.user.userName != "Bookmarks") {
                chat = chat.copy(messages = listOf(Message()))
            }
            putExtra(
                CHAT,
                chat.copy(
                    user = chat.user.copy(
                        userImage = chat.user.userImage.copy(
                            userImageBitmap = Bitmap.createBitmap(
                                1, 1, Bitmap.Config.ARGB_8888
                            )
                        )
                    )
                )
            )
            messengerViewModel.viewModelScope.launch {
                putExtra(TOKEN, messengerViewModel.me.value.userToken)
            }
        }
        return chatIntent
    }

    @Composable
    private fun ProgressIndicator() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colors.secondary
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRestart() {
        super.onRestart()
        messengerViewModel.loadViewData()
    }
}