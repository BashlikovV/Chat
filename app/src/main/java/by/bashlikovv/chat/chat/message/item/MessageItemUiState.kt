package by.bashlikovv.chat.chat.message.item

/**
 * [MessageItemUiState] contains data to present message from user.
 * * [image] -> image of user;
 * * [message] -> message from user;
 * * [time] -> time when user send this message
 * */

data class MessageItemUiState(
    val image: Int = 0,
    val message: String = "",
    val time: String = ""
)
