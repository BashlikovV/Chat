package by.bashlikovv.chat.model

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import by.bashlikovv.chat.login.LogInUiState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LogInViewModel : ViewModel() {

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

    fun onCheckInput(context: Context, haveAccount: Boolean) {
        val email = _logInUiState.value.identifier
        val password = _logInUiState.value.password
        try {
            if (haveAccount) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        _logInUiState.update {
                            it.copy(isPasswordCorrect = true, isIdentifierCorrect = true, isSuccess = true)
                        }
                    }
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            _logInUiState.update { it.copy(isPasswordCorrect = false, isIdentifierCorrect = false) }
                            return@addOnCompleteListener
                        }
                        _logInUiState.update {
                            it.copy(isPasswordCorrect = true, isIdentifierCorrect = true, isSuccess = true)
                        }
                        Toast
                            .makeText(context, "Success. New user: ${task.result.user?.email}", Toast.LENGTH_LONG)
                            .show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
                    }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Please, fill this fields", Toast.LENGTH_SHORT).show()
            _logInUiState.update { it.copy(isPasswordCorrect = false, isIdentifierCorrect = false) }
        }
    }

    fun onButtonClk(newValue: Boolean) {
        _logInUiState.update { it.copy(isHaveAccount = newValue) }
    }
}