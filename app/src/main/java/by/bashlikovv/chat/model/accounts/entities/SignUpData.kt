package by.bashlikovv.chat.model.accounts.entities

import by.bashlikovv.chat.model.EmptyFieldException
import by.bashlikovv.chat.model.Field

data class SignUpData(
    val username: String,
    val email: String,
    val password: String
) {
    fun validate() {
        if (email.isBlank()) throw EmptyFieldException(Field.Email)
        if (username.isBlank()) throw EmptyFieldException(Field.Username)
        if (password.isBlank()) throw EmptyFieldException(Field.Password)
    }
}
