package by.bashlikovv.chat.chat.message

import by.bashlikovv.chat.R
import by.bashlikovv.chat.chat.list.ChatListUiTestData
import by.bashlikovv.chat.chat.message.item.MessageItemUiState

object MessageListTestData {
    private val tmpMessages = (0..30).map {
        MessageItemUiState(
            image = listOf(R.drawable.test_face_man, R.drawable.test_face_woman).random(),
            message = "message $it",
            time = ChatListUiTestData.getRandomTime()
        )
    }

    val tmpData = MessagesListUiSate(
        userImage = listOf(R.drawable.test_face_man, R.drawable.test_face_woman).random(),
        userName = "User name",
        isOnline = false,
        messages = tmpMessages
    )
}