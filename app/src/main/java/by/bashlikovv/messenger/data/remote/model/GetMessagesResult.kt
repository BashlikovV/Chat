package by.bashlikovv.messenger.data.remote.model

import by.bashlikovv.messenger.domain.model.Message

data class GetMessagesResult(
    val messages: List<Message> = listOf(),
    val unreadMessageCount: Int = 0
)