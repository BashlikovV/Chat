package by.bashlikovv.chat

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import by.bashlikovv.chat.login.LogInView
import by.bashlikovv.chat.model.LogInViewModel
import by.bashlikovv.chat.theme.MessengerTheme

class LogInActivity : ComponentActivity() {
    private val logInViewModel: LogInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val logInUiState by logInViewModel.logInUiState.collectAsState()

            MessengerTheme(darkTheme = true) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    LogInView()

                    if (logInUiState.isSuccess) {
                        val messengerIntent = Intent(this, MessengerActivity::class.java).apply {
                            putExtra(USER_NAME, logInUiState.userName)
                            putExtra(USER_EMAIL, logInUiState.identifier)
                            putExtra(USER_PASSWORD, logInUiState.password)
                        }
                        startActivity(messengerIntent)
                        finish()
                    }
                }
            }
        }
    }

    companion object {
        const val USER_NAME = "user_name"
        const val USER_EMAIL = "user_email"
        const val USER_PASSWORD = "user_password"
    }
}