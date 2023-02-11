package by.bashlikovv.chat.chat.list

import by.bashlikovv.chat.R
import by.bashlikovv.chat.chat.message.MessageListTestData

object ChatListUiTestData {
    val chatListTestData = (0..90).map {
        ChatListUiState(
            image = listOf(R.drawable.test_face_man, R.drawable.test_face_woman).random(),
            name = "user name $it",
            displayedMessage = "displayed message $it",
            time = getRandomTime(),
            unreadMessagesCount = (0..20).random(),
            messagesListUiSate = MessageListTestData.tmpData
        )
    }

    fun getRandomTime(): String {
        val hh: String
        val mm: String
        var tmp = (0..12).random()
        hh = if (tmp < 10) {
            "0$tmp"
        } else {
            tmp.toString()
        }
        tmp = (1..60).random()
        mm = if (tmp < 10) {
            "0$tmp"
        } else {
            tmp.toString()
        }
        return "$hh:$mm"
    }
}