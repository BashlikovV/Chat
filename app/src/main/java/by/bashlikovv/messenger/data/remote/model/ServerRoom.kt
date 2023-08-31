package by.bashlikovv.messenger.data.remote.model

data class ServerRoom(
    val user1: ServerUser = ServerUser(),
    val user2: ServerUser = ServerUser(),
    val token: ByteArray = byteArrayOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServerRoom

        if (user1 != other.user1) return false
        if (user2 != other.user2) return false
        return token.contentEquals(other.token)
    }

    override fun hashCode(): Int {
        var result = user1.hashCode()
        result = 31 * result + user2.hashCode()
        result = 31 * result + token.contentHashCode()
        return result
    }
}
