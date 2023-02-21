package by.bashlikovv.chat.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ChatView(
    modifier: Modifier = Modifier, chatViewModel: ChatViewModel = viewModel()
) {
    val chatUiState by chatViewModel.chatUiState.collectAsState()


}