package by.bashlikovv.chat

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
import by.bashlikovv.chat.messenger.MessengerView
import by.bashlikovv.chat.messenger.MessengerViewModel
import by.bashlikovv.chat.theme.MessengerTheme

class MessengerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val messengerViewModel: MessengerViewModel by viewModels()
            val messengerUiState by messengerViewModel.messengerUiState.collectAsState()

            MessengerTheme(darkTheme = messengerUiState.darkTheme) {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.primary) {
                    MessengerView()
                }
            }
        }
    }
}