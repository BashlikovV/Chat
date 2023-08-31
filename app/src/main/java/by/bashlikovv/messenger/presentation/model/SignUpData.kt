package by.bashlikovv.messenger.presentation.model

import by.bashlikovv.messenger.data.EmptyFieldException

data class SignUpData(
    val username: String,
    val email: String,
    val password: String
) {
    fun validate() {
        if (email.isBlank()) throw EmptyFieldException()
        if (username.isBlank()) throw EmptyFieldException()
        if (password.isBlank()) throw EmptyFieldException()
    }
}
