package by.bashlikovv.chat.login

data class LogInUiState(
    val identifier: String = "",
    val password: String = "",
    val userName: String = "",
    val isIdentifierCorrect: Boolean = true,
    val isPasswordCorrect: Boolean = true,
    val isSuccess: Boolean = false,
    val isHaveAccount: Boolean = false
)
