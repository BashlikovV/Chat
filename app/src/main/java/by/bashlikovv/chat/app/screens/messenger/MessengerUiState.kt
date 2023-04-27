package by.bashlikovv.chat.app.screens.messenger

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import by.bashlikovv.chat.app.struct.Chat

data class MessengerUiState(
    val chats: List<Chat> = emptyList(),
    val visible: Boolean = false,
    val selectedItem: Chat = Chat(),
    val drawerState: DrawerState = DrawerState(DrawerValue.Closed),
    val darkTheme: Boolean = true,
    val expanded: Boolean = false,
    val newChat: Boolean = false,
    val searchInput: String = "",
    val searchedItems: List<Chat> = emptyList()
)