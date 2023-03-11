package by.bashlikovv.chat.screens.messenger

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import by.bashlikovv.chat.R
import by.bashlikovv.chat.screens.login.UserImage
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
    private val userTestManBitmap = R.drawable.test_face_man.toDrawable().toBitmap(50, 50)
    private val userTestWomanBitmap = R.drawable.test_face_man.toDrawable().toBitmap(50, 50)
    private val me = User(userId = 0, userName = "appice", userImage = UserImage(userImageBitmap = userTestManBitmap))
    val testData = (0..30).map { chatIndex ->
        Chat(
            user = User(
                userId = chatIndex.toLong(),
                userImage = UserImage(
                    userImageBitmap = listOf(userTestWomanBitmap, userTestManBitmap).random()
                ),
                userName = "user$chatIndex"
            ),
            messages = (0..30).map {
                Message(
                    value = "Message from user${chatIndex + 1}",
                    user = listOf(
                        me, User(
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