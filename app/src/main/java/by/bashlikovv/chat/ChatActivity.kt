package by.bashlikovv.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.content.MainContent
import by.bashlikovv.chat.nav.DrawerViewModel
import by.bashlikovv.chat.ui.theme.ChatTheme

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val drawerViewModel: DrawerViewModel = viewModel()
            val drawerUiState by drawerViewModel.drawerState.collectAsState()

            ChatTheme(darkTheme = drawerUiState.darkTheme) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.primary) {
                    MainContent(drawerViewModel = drawerViewModel)
                }
            }
        }
    }
}