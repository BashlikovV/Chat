package by.bashlikovv.chat.model

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import by.bashlikovv.chat.R
import by.bashlikovv.chat.struct.Chat
import by.bashlikovv.chat.struct.Message
import by.bashlikovv.chat.struct.User

data class MessengerUiState(
    val chats: List<Chat> = emptyList(),
    val visible: Boolean = false,
    val selectedItem: Chat = Chat(),
    val drawerState: DrawerState = DrawerState(DrawerValue.Closed),
    val darkTheme: Boolean = true,
    val expanded: Boolean = false,
    val searchInput: String = ""
)

object MessengerTestData {
    val testData = (0..30).map { chatIndex ->
        Chat(
            user = User(
                userId = chatIndex.toLong(),
                userImage = listOf(R.drawable.test_face_man, R.drawable.test_face_woman).random(),
                userName = "user$chatIndex"
            ),
            messages = (0..30).map {
                Message(
                    value = "Message from user$chatIndex",
                    user = User(
                        userId = chatIndex.toLong(),
                        userImage = listOf(R.drawable.test_face_man, R.drawable.test_face_woman).random(),
                        userName = "user$chatIndex"
                    ),
                    isRead = listOf(true, false).random(),
                    time = "22:22:22"
                )
            },
            count = (0..15).random()
        )
    }
}