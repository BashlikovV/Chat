package by.bashlikovv.messenger.presentation.view.login

data class LogInUiState(
    val identifier: String = "",
    val password: String = "",
    val userName: String = "",
    val userImageBitmap: String = "",
    val isIdentifierCorrect: Boolean = true,
    val isPasswordCorrect: Boolean = true,
    val isSuccess: Boolean = false,
    val isHaveAccount: Boolean = false,
    val progressBarVisibility: Boolean = false,
    val token: String = ""
)
