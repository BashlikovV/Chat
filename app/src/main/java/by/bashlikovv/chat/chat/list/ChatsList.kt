package by.bashlikovv.chat.chat.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.R
import by.bashlikovv.chat.chat.list.item.ChatItem
import by.bashlikovv.chat.navbar.TopNavBar
import by.bashlikovv.chat.navbar.TopNavBarState
import by.bashlikovv.chat.navbar.TopNavBarViewModel
import by.bashlikovv.chat.ui.theme.Teal200

@Composable
fun ChatsList(
    topNavBarViewModel: TopNavBarViewModel = viewModel()
) {
//    val topNavBarState by topNavBarViewModel.topNavBarState.collectAsState()

    Scaffold(
        topBar = {
            TopNavBar(
                modifier = Modifier
                    .background(Teal200)
            ) {
                topNavBarViewModel.onBarChange(TopNavBarViewModel.MENU_IMAGE, TopNavBarViewModel.MENU_DESCRIPTION)
            }
        }
    ) {
        ChatListContent(modifier = Modifier.padding(it))
    }
}

@Composable
fun ChatListContent(
    modifier: Modifier = Modifier,
    chatListViewModel: ChatListViewModel = viewModel(),
    topNavBarViewModel: TopNavBarViewModel = viewModel()
) {
    val chatListUiState by chatListViewModel.chatListUiState.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        state = rememberLazyGridState(),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalArrangement = Arrangement.Center,
        userScrollEnabled = true,
        modifier = modifier
            .fillMaxSize(fraction = 1f)
    ) {
        items(chatListUiState) { listItem ->
            ChatItem(
                data = listItem,
                onChatsItemSelect = {
                    chatListViewModel.onChatListItemClicked(it)
                },
                onLongPress = {
                    topNavBarViewModel.onBarChange(TopNavBarViewModel.CLOSE_IMAGE, TopNavBarViewModel.CLOSE_DESCRIPTION)
                }
            )
        }
    }
}