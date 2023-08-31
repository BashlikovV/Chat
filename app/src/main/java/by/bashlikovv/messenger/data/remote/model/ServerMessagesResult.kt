package by.bashlikovv.messenger.data.remote.model

data class GetServerMessagesResult(
    val serverMessages: List<ServerMessage> = listOf(),
    val unreadMessagesCount: Int = 0
)