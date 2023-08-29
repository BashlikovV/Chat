package by.bashlikovv.messenger.presentation.view

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import by.bashlikovv.messenger.domain.model.Chat
import by.bashlikovv.messenger.presentation.view.chat.ChatView
import by.bashlikovv.messenger.presentation.view.theme.MessengerTheme
import by.bashlikovv.messenger.presentation.viewmodel.ChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val chatViewModel: ChatViewModel by viewModel()
        backPressListener(chatViewModel)
        val darkTheme = intent.extras?.getBoolean(MessengerActivity.DARK_THEME)
        val data = intent.extras?.getParcelable<Chat>(MessengerActivity.CHAT)
        val token = intent.extras?.getString(MessengerActivity.TOKEN) ?: ""
        if (data != null) {
            chatViewModel.applyChatData(data)
            chatViewModel.applyMe(token)
        }

        setContent {
            val updateVisibility by chatViewModel.updateVisibility.collectAsState()
            suspend fun update() {
                chatViewModel.setUpdateVisibility(true)
                val result = suspendCancellableCoroutine {
                    chatViewModel.viewModelScope.launch(Dispatchers.IO) {
                        chatViewModel.getMessagesFromDb()
                        it.resumeWith(Result.success(false))
                    }
                }
                chatViewModel.setUpdateVisibility(result)
            }
            if (data?.user?.userName != "Bookmarks") {
                LaunchedEffect(Unit) { update() }
            }
            MessengerTheme(darkTheme = darkTheme ?: true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ChatView { onBackPressedDispatcher.onBackPressed() }
                        if (updateVisibility) { ProgressIndicator() }
                    }
                }
            }
        }
    }

    private fun backPressListener(chatViewModel: ChatViewModel) {
        onBackPressedDispatcher.addCallback {
            if (chatViewModel.selectedItemsState.value.containsValue(true)) {
                chatViewModel.clearSelectedMessages()
                return@addCallback
            } else {
                finish()
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