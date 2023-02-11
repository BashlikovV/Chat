package by.bashlikovv.chat.chat.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Scaffold
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.R
import by.bashlikovv.chat.chat.message.item.MessagesItem
import by.bashlikovv.chat.content.MainContentViewModel

/**
 * [MessagesList] -> list of messages between two users
 * * [messagesListViewModel] -> contains input value of the current chat
 * * [mainContentViewModel] -> contains list of messages between two users
 * mainContentUiState.data.messages -> list of messages
 * */

@Composable
fun MessagesList(
    modifier: Modifier = Modifier,
    messagesListViewModel: MessagesListViewModel = viewModel(),
    mainContentViewModel: MainContentViewModel = viewModel(),
    navigateBack: () -> Unit
) {
    val messagesListUiSate by messagesListViewModel.messagesListUiSate.collectAsState()
    val mainContentUiState by mainContentViewModel.mainContentUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar {
                Image(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "back",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(35.dp)
                        .clickable {
                            navigateBack()
                        }
                )
            }
        },
        bottomBar = {
            TextField(
                value = messagesListUiSate.input,
                onValueChange = {
                    messagesListViewModel.onChangeInput(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        messagesListViewModel.onActionSend()
                    }
                )
            )
        }
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                items(mainContentUiState.data.messages) { message ->
                    MessagesItem(message)
                }
            }
        }
    }
}