package by.bashlikovv.messenger.data.remote.request

data class SignUpRequestBody(
    val username: String,
    val email: String,
    val password: String,
    val image: String
)