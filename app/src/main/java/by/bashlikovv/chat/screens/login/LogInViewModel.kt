package by.bashlikovv.chat.screens.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import by.bashlikovv.chat.Repositories
import by.bashlikovv.chat.model.AccountAlreadyExistsException
import by.bashlikovv.chat.model.EmptyFieldException
import by.bashlikovv.chat.model.PasswordMismatchException
import by.bashlikovv.chat.model.StorageException
import by.bashlikovv.chat.model.accounts.AccountsRepository
import by.bashlikovv.chat.model.accounts.entities.SignUpData
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

class LogInViewModel(
    private val accountsRepository: AccountsRepository
) : ViewModel() {

    private val _logInUiState = MutableStateFlow(LogInUiState())
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

    fun selectImage(activity: ComponentActivity) {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        activity.startActivityForResult(intent, 0)
    }

    fun applyUserImageUri(newValue: Uri) {
        val value = _logInUiState.value.userImageBitmap.copy(userImageUri = newValue)
        _logInUiState.update { it.copy(userImageBitmap = value) }
    }

    fun applyUserBitmapImage(bitmap: ImageBitmap) {
        val fileName = UUID.randomUUID()
        val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")
        val value = _logInUiState.value.userImageBitmap.copy(
            userImageBitmap = bitmap.asAndroidBitmap(), userImageUrl = ref.downloadUrl.toString()
        )
        ref.putFile(_logInUiState.value.userImageBitmap.userImageUri)
        _logInUiState.update { it.copy(userImageBitmap = value) }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun onCreateAccountButtonPressed(context: Context) {
        try {
            if (_logInUiState.value.isHaveAccount) {
                GlobalScope.launch(Dispatchers.Main) {
                    accountsRepository.signIn(_logInUiState.value.identifier, _logInUiState.value.password)
                }
            } else {
                val signUpData = SignUpData(
                    email = _logInUiState.value.identifier,
                    username = _logInUiState.value.userName,
                    password = _logInUiState.value.password,
                )
                GlobalScope.launch(Dispatchers.Main) {
                    signUp(signUpData, context)
                    accountsRepository.signIn(_logInUiState.value.identifier, _logInUiState.value.password)
                }
            }
        } catch (e: Exception) {
            showToast(context, "Authentication error ${e.message}")
        } finally {
            GlobalScope.launch(Dispatchers.Main) {
                if (accountsRepository.isSignedIn()) {
                    applySuccess()
                }
            }
        }
    }

    private suspend fun signUp(signUpData: SignUpData, context: Context) {
        _logInUiState.update { it.copy(progressBarVisibility = true) }
        try {
            accountsRepository.signUp(signUpData)
            showSuccessSignUpMessage(context)
        } catch (e: EmptyFieldException) {
            processEmptyFieldException(e)
        } catch (e: PasswordMismatchException) {
            processPasswordMismatchException(context)
        } catch (e: AccountAlreadyExistsException) {
            processAccountAlreadyExistsException(context)
        } catch (e: StorageException) {
            processStorageException(context)
        } finally {
            hideProgress()
        }
        _logInUiState.update { it.copy(progressBarVisibility = false) }
    }

    private fun showToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    private fun processStorageException(context: Context) {
        showToast(context, "Storage process exception")
    }

    private fun hideProgress() {
        _logInUiState.update { it.copy(progressBarVisibility = false) }
    }

    private fun processAccountAlreadyExistsException(context: Context) {
        showToast(context, "Account already exists")
    }

    private fun processPasswordMismatchException(context: Context) {
        showToast(context, "Error. Incorrect password")
    }

    private fun processEmptyFieldException(e: EmptyFieldException) {
        TODO("Not yet implemented")
    }

    private fun showSuccessSignUpMessage(context: Context) {
        showToast(context, "Success!")
    }
}