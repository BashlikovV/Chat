package by.bashlikovv.messenger.data.local.model

data class Account(
    val id: Long,
    val username: String,
    val email: String,
    val token: String,
    val createdAt: Long = UNKNOWN_CREATED_AT
) {

    companion object {
        const val UNKNOWN_CREATED_AT = 0L
    }
}
