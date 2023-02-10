package by.bashlikovv.chat.chat.list

import by.bashlikovv.chat.R

object ChatListUiTestData {
    val chatListTestData = (0..9).map {
        ChatListUiState(
            image = listOf(R.drawable.test_face_man, R.drawable.test_face_woman).random(),
            name = "user name $it",
            displayedMessage = "displayed message $it",
            time = getRandomTime()
        )
    }

    fun getRandomTime(): String {
        val hh: String
        val mm: String
        val ss: String
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
        tmp = (1..60).random()
        ss = if (tmp < 10) {
            "0$tmp"
        } else {
            tmp.toString()
        }
        return "$hh:$mm:$ss"
    }
}