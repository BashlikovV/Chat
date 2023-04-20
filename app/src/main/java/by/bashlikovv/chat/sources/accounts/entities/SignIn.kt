package by.bashlikovv.chat.sources.accounts.entities

data class SignInRequestBody(
    val email: String,
    val password: String
)

data class SignInResponseBody(
    val token: String
)