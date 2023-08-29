package by.bashlikovv.messenger.data.remote.response

data class AddImageResponseBody(
    val imageUri: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AddImageResponseBody

        return imageUri.contentEquals(other.imageUri)
    }

    override fun hashCode(): Int {
        return imageUri.contentHashCode()
    }
}