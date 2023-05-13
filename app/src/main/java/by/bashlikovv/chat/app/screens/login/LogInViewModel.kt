package by.bashlikovv.chat.app.screens.login

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.bashlikovv.chat.app.model.AccountAlreadyExistsException
import by.bashlikovv.chat.app.model.EmptyFieldException
import by.bashlikovv.chat.app.model.PasswordMismatchException
import by.bashlikovv.chat.app.model.StorageException
import by.bashlikovv.chat.app.model.accounts.AccountsRepository
import by.bashlikovv.chat.app.model.accounts.entities.SignUpData
import by.bashlikovv.chat.app.model.users.OkHTTPUsersRepository
import by.bashlikovv.chat.app.model.users.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*

class LogInViewModel(
    private val accountsRepository: AccountsRepository,
    private val usersRepository: UsersRepository = OkHTTPUsersRepository()
) : ViewModel() {

    private val _logInUiState = MutableStateFlow(LogInUiState(progressBarVisibility = true))
    val logInUiState = _logInUiState.asStateFlow()

    fun onIdentifierChange(newValue: String) {
        _logInUiState.update { it.copy(identifier = newValue) }
    }

    fun onPasswordChange(newValue: String) {
        _logInUiState.update { it.copy(password = newValue) }
    }

    fun onUserNameChange(newValue: String) {
        _logInUiState.update { it.copy(userName = newValue) }
    }

    fun applySuccess() {
        _logInUiState.update { it.copy(isSuccess = true) }
    }

    fun onButtonClk(newValue: Boolean) {
        _logInUiState.update { it.copy(isHaveAccount = newValue) }
    }

    fun applyUserImage(context: Context, imageUri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri).asImageBitmap()
            val value = _logInUiState.value.userImageBitmap.copy(
                userImageUri = imageUri,
                userImageBitmap = bitmap.asAndroidBitmap(),
                userImageUrl = imageUri.path.toString()
            )
            _logInUiState.update { it.copy(userImageBitmap = value) }
        } catch (e: Exception) {
            showToast(context, "Image not found")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onCreateAccountButtonPressed(context: Context) {
        _logInUiState.update { it.copy(progressBarVisibility = true) }
        viewModelScope.launch(Dispatchers.Main) {
            val result = suspendCancellableCoroutine {
                viewModelScope.launch(Dispatchers.Main) {
                    val token: String
                    try {
                        if (_logInUiState.value.isHaveAccount) {
                            token = usersRepository.signIn(
                                _logInUiState.value.identifier,
                                _logInUiState.value.password
                            )
                            if (token.contains("500")) {
                                showToast(context, "Network error")
                            }
                            _logInUiState.update { it.copy(token = token) }
                            if (!_logInUiState.value.token.contains("500")) {
                                if (!accountsRepository.isSignedIn()) {
                                    val signUpData = SignUpData(
                                        email = _logInUiState.value.identifier,
                                        username = usersRepository.getUsername(token),
                                        password = _logInUiState.value.password,
                                    )
                                    signUp(signUpData, context)
                                }
                                accountsRepository.signIn(
                                    _logInUiState.value.identifier,
                                    _logInUiState.value.password
                                )
                            } else {
                                processPasswordMismatchException(context)
                                showToast(context, "Authentication error.")
                            }
                        } else {
                            val signUpData = SignUpData(
                                email = _logInUiState.value.identifier,
                                username = _logInUiState.value.userName,
                                password = _logInUiState.value.password,
                            )
                            signUp(signUpData, context)
                        }
                    } catch (e: Exception) {
                        processPasswordMismatchException(context)
                        showToast(context, "Authentication error ${e.message}")
                    } finally {
                        if (accountsRepository.isSignedIn() && !_logInUiState.value.token.contains("500")) {
                            applySuccess()
                            showToast(context, "token: ${_logInUiState.value.token}")
                        } else {
                            showToast(context, "Authentication error.")
                        }
                    }
                    it.resumeWith(Result.success(false))
                }
            }
            _logInUiState.update { it.copy(progressBarVisibility = result) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun signUp(signUpData: SignUpData, context: Context) {
        try {
            usersRepository.signUp(
                email = signUpData.email,
                password = signUpData.password,
                username = signUpData.username,
                image = _logInUiState.value.userImageBitmap.userImageBitmap
            )
            val token: String = usersRepository.signIn(
                email = signUpData.email, password = signUpData.password
            )
            _logInUiState.update { it.copy(token = token) }
            if (!_logInUiState.value.token.contains("500")) {
                accountsRepository.signUp(signUpData, _logInUiState.value.token)
                showSuccessSignUpMessage(context)
            } else {
                showToast(context, "Authentication error.")
                throw Exception()
            }
        } catch (e: EmptyFieldException) {
            processEmptyFieldException()
        } catch (e: PasswordMismatchException) {
            processPasswordMismatchException(context)
        } catch (e: AccountAlreadyExistsException) {
            processAccountAlreadyExistsException(context)
        } catch (e: StorageException) {
            processStorageException(context)
        }
    }

    private fun showToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    private fun processStorageException(context: Context) {
        showToast(context, "Storage process exception")
    }

    private fun processAccountAlreadyExistsException(context: Context) {
        _logInUiState.update { it.copy(isIdentifierCorrect = false) }
        showToast(context, "Account already exists")
    }

    private fun processPasswordMismatchException(context: Context) {
        _logInUiState.update { it.copy(isPasswordCorrect = false) }
        showToast(context, "Error. Incorrect password")
    }

    private fun processEmptyFieldException() {
        TODO("Not yet implemented")
    }

    private fun showSuccessSignUpMessage(context: Context) {
        showToast(context, "Success!")
    }

    fun setProgressVisibility(newValue: Boolean) {
        _logInUiState.update { it.copy(progressBarVisibility = newValue) }
    }

    fun clearInputErrors() {
        _logInUiState.update { it.copy(
            isIdentifierCorrect = true,
            isPasswordCorrect = true
        ) }
    }
}