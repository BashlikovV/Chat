package by.bashlikovv.chat.app.screens.messenger.users

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.Repositories
import by.bashlikovv.chat.app.screens.messenger.MessengerViewModel
import by.bashlikovv.chat.app.screens.messenger.chats.UserImageView
import by.bashlikovv.chat.app.screens.messenger.chats.UserNameView
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.utils.viewModelCreator

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UsersView(
    modifier: Modifier = Modifier,
    messengerViewModel: MessengerViewModel = viewModel(LocalContext.current as ComponentActivity),
    onCreateChat: (Chat) -> Unit
) {
    val activity = LocalContext.current as ComponentActivity
    val usersViewModel: UsersViewModel by activity.viewModelCreator {
        UsersViewModel(Repositories.accountsRepository, messengerViewModel)
    }
    val usersUiState by usersViewModel.usersUiState.collectAsState()
    val messengerUiState by messengerViewModel.messengerUiState.collectAsState()

    LaunchedEffect(key1 = messengerUiState.searchInput) {
        usersViewModel.getUsers(messengerUiState.searchInput)
    }

    LazyColumn(
        modifier = modifier.background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (usersUiState.chats.isNotEmpty()) {
            items(usersUiState.chats) { chat ->
                UsersItem(chat = chat) { onCreateChat(it) }
            }
        }
    }
}

@Composable
fun UsersItem(chat: Chat, modifier: Modifier = Modifier, onCreateChat: (Chat) -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 10.dp)
            .background(MaterialTheme.colors.background)
            .clickable { onCreateChat(chat) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 12.dp)
        ) {
            UserImageView(
                chat.user.userImage.userImageBitmap,
                username = chat.user.userName
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                UserNameView(
                    username = chat.user.userName,
                    modifier = Modifier.weight(0.9f)
                )
            }
        }
    }
}