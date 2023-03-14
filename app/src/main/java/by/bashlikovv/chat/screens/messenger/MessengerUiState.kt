package by.bashlikovv.chat.screens.messenger

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import by.bashlikovv.chat.struct.Chat
import by.bashlikovv.chat.struct.User

data class MessengerUiState(
    val chats: List<Chat> = emptyList(),
    val me: User = User(),
    val visible: Boolean = false,
    val selectedItem: Chat = Chat(),
    val drawerState: DrawerState = DrawerState(DrawerValue.Closed),
    val darkTheme: Boolean = true,
    val expanded: Boolean = false,
    val searchInput: String = "",
    val searchedItems: List<Chat> = emptyList()
)