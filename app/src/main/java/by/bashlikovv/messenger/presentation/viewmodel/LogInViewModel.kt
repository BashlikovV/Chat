package by.bashlikovv.messenger.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.bashlikovv.messenger.data.AccountAlreadyExistsException
import by.bashlikovv.messenger.data.EmptyFieldException
import by.bashlikovv.messenger.data.PasswordMismatchException
import by.bashlikovv.messenger.data.StorageException
import by.bashlikovv.messenger.domain.usecase.CheckSignedInUseCase
import by.bashlikovv.messenger.domain.usecase.GetUsernameOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.SignInOfflineUseCase
import by.bashlikovv.messenger.domain.usecase.SignInOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.SignUpOfflineUseCase
import by.bashlikovv.messenger.domain.usecase.SignUpOnlineUseCase
import by.bashlikovv.messenger.presentation.model.SignUpData
import by.bashlikovv.messenger.presentation.view.login.LogInUiState
import by.bashlikovv.messenger.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*

class LogInViewModel(
    private val signInOnlineUseCase: SignInOnlineUseCase,
    private val signInOfflineUseCase: SignInOfflineUseCase,
    private val checkSignedInUseCase: CheckSignedInUseCase,
    private val getUsernameOnlineUseCase: GetUsernameOnlineUseCase,
    private val signUpOnlineUseCase: SignUpOnlineUseCase,
    private val signUpOfflineUseCase: SignUpOfflineUseCase
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
            showToast(context, R.string.image_not_found_error)
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
                            token = signInOnlineUseCase.execute(
                                _logInUiState.value.identifier,
                                _logInUiState.value.password
                            )
                            if (token.contains("500")) {
                                showToast(context, R.string.network_error)
                            }
                            _logInUiState.update { it.copy(token = token) }
                            if (!_logInUiState.value.token.contains("500")) {
                                if (!checkSignedInUseCase.execute()) {
                                    val username =  getUsernameOnlineUseCase.execute(token)
                                    val signUpData = SignUpData(
                                        email = _logInUiState.value.identifier,
                                        username = username,
                                        password = _logInUiState.value.password,
                                    )
                                    signUp(signUpData, context)
                                }
                                signInOfflineUseCase.execute(
                                    _logInUiState.value.identifier,
                                    _logInUiState.value.password
                                )
                            } else {
                                processPasswordMismatchException(context)
                                showToast(context, R.string.authentication_error)
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
                        showToast(
                            context,
                            R.string.authentication_with_message_error, e.message ?: "null"
                        )
                    } finally {
                        if (checkSignedInUseCase.execute() && !_logInUiState.value.token.contains("500")) {
                            applySuccess()
                            showToast(
                                context,
                                R.string.token_value, _logInUiState.value.token
                            )
                        } else {
                            showToast(context, R.string.authentication_error)
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
            signUpOnlineUseCase.execute(
                email = signUpData.email,
                password = signUpData.password,
                username = signUpData.username,
                image = _logInUiState.value.userImageBitmap.userImageBitmap
            )
//            _logInUiState.update { it.copy(token = Repositories.myToken) }
            if (!_logInUiState.value.token.contains("500")) {
                try {
                    signUpOfflineUseCase.execute(signUpData, _logInUiState.value.token)
                } catch (_: AccountAlreadyExistsException) {
                } catch (e: Exception) {
                    throw e
                }
                showSuccessSignUpMessage(context)
            } else {
                showToast(context, R.string.authentication_error)
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

    private fun showToast(context: Context, @StringRes text: Int, vararg data: String) {
        Toast.makeText(context, context.getString(text, *data), Toast.LENGTH_LONG).show()
    }

    private fun processStorageException(context: Context) {
        showToast(context, R.string.storage_process_error)
    }

    private fun processAccountAlreadyExistsException(context: Context) {
        _logInUiState.update { it.copy(isIdentifierCorrect = false) }
        showToast(context, R.string.account_already_exists_error)
    }

    private fun processPasswordMismatchException(context: Context) {
        _logInUiState.update { it.copy(isPasswordCorrect = false) }
        showToast(context, R.string.authentication_error)
    }

    private fun processEmptyFieldException() {
        TODO("Not yet implemented")
    }

    private fun showSuccessSignUpMessage(context: Context) {
        showToast(context, R.string.success_message)
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