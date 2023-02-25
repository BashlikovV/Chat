package by.bashlikovv.chat.model

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import by.bashlikovv.chat.R
import by.bashlikovv.chat.struct.Chat
import by.bashlikovv.chat.struct.Message
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

object MessengerTestData {
    private val me = User(userId = 0, userName = "appice", userImage = R.drawable.test_face_man)
    val testData = (0..30).map { chatIndex ->
        Chat(
            user = User(
                userId = chatIndex.toLong(),
                userImage = listOf(R.drawable.test_face_man, R.drawable.test_face_woman).random(),
                userName = "user$chatIndex"
            ),
            messages = (0..30).map {
                Message(
                    value = "Message from user${chatIndex + 1}",
                    user = listOf(me, User(
                        userId = chatIndex.toLong(),
                        userName = "user name $chatIndex"
                    )).random(),
                    isRead = listOf(true, false).random(),
                    time = "22:22"
                )
            },
            count = (0..15).random()
        )
    }
}