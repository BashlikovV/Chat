package by.bashlikovv.chat.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.struct.Message

@Composable
fun ChatView(modifier: Modifier = Modifier, onBackAction: () -> Unit) {
    Scaffold(topBar = { TopChatBar { onBackAction() } }, bottomBar = { BottomInputFiled() }) {
        ChatContent(modifier = modifier.padding(it).fillMaxSize())
    }
}

@Composable
fun ChatContent(modifier: Modifier = Modifier, chatViewModel: ChatViewModel = viewModel()) {
    val chatUiState by chatViewModel.chatUiState.collectAsState()

    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(1.dp)) {
        items(chatUiState.chat.messages) {
            MessageView(message = it)
        }
    }
}

@Composable
fun MessageView(message: Message, chatViewModel: ChatViewModel = viewModel()) {
    val chatUiState by chatViewModel.chatUiState.collectAsState()

    Row(
        horizontalArrangement = if (message.user.userName == chatUiState.usersData.first().userName)
            Arrangement.Start
        else
            Arrangement.End,
        modifier = Modifier.padding(
            horizontal = 5.dp,
            vertical = 2.5.dp
        ).fillMaxSize()
    ) {
        Row(modifier = Modifier.clip(RoundedCornerShape(10.dp)).padding(5.dp).background(MaterialTheme.colors.primary)) {
            Text(
                text = message.value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colors.primaryVariant,
            )
            Text(
                text = message.time,
                fontSize = 12.sp,
                fontWeight = FontWeight.Thin,
                color = MaterialTheme.colors.primaryVariant
            )
        }
    }
}