package by.bashlikovv.chat.sources.structs

data class ServerMessage(
    val room: ServerRoom = ServerRoom(),
    val image: String = "",
    val value: ByteArray = byteArrayOf(),
    val file: ByteArray = byteArrayOf(),
    val owner: ServerUser = ServerUser(),
    val time: String = "",
    val from: String = "",
    val isRead: Boolean = true
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServerMessage

        if (room != other.room) return false
        if (image != other.image) return false
        if (!value.contentEquals(other.value)) return false
        if (!file.contentEquals(other.file)) return false
        if (owner != other.owner) return false
        if (time != other.time) return false
        if (isRead != other.isRead) return false
        return from == other.from
    }

    override fun hashCode(): Int {
        var result = room.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + value.contentHashCode()
        result = 31 * result + file.contentHashCode()
        result = 31 * result + owner.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + from.hashCode()
        result = 31 * result + isRead.hashCode()
        return result
    }
}
