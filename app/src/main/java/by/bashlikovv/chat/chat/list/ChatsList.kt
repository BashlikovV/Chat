package by.bashlikovv.chat.chat.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.chat.list.item.ChatItem
import by.bashlikovv.chat.nav.DrawerContent
import by.bashlikovv.chat.nav.DrawerViewModel
import by.bashlikovv.chat.navbar.TopNavBar
import by.bashlikovv.chat.navbar.TopNavBarViewModel

@Composable
fun ChatsList(
    topNavBarViewModel: TopNavBarViewModel = viewModel(),
    drawerViewModel: DrawerViewModel = viewModel(),
    navigateToMessage: (ChatListUiState) -> Unit
) {
    val drawerUiState by drawerViewModel.drawerState.collectAsState()

    ModalDrawer(
        drawerState = drawerUiState.drawerState,
        drawerContent = {
            DrawerContent(drawerUiState)
        }
    ) {
        Scaffold(
            topBar = {
                TopNavBar(
                    modifier = Modifier
                        .background(MaterialTheme.colors.onSurface)
                ) {
                    if (topNavBarViewModel.topNavBarState.value.description == TopNavBarViewModel.MENU_DESCRIPTION) {
                        drawerViewModel.openDrawer()
                    }
                    topNavBarViewModel.onBarChange(TopNavBarViewModel.MENU_IMAGE, TopNavBarViewModel.MENU_DESCRIPTION)
                }
            }
        ) { paddingValues ->
            ChatListContent(modifier = Modifier.padding(paddingValues)) {
                navigateToMessage(it)
            }
        }
    }


}

@Composable
fun ChatListContent(
    modifier: Modifier = Modifier,
    chatListViewModel: ChatListViewModel = viewModel(),
    topNavBarViewModel: TopNavBarViewModel = viewModel(),
    navigateToMessage: (ChatListUiState) -> Unit
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
                    navigateToMessage(it)
                },
                onLongPress = { data ->
                    chatListViewModel.onPressSelect(data)
                    topNavBarViewModel.onBarChange(TopNavBarViewModel.CLOSE_IMAGE, TopNavBarViewModel.CLOSE_DESCRIPTION)
                }
            )
        }
    }
}