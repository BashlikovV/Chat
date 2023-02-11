package by.bashlikovv.chat.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import by.bashlikovv.chat.chat.list.ChatsList
import by.bashlikovv.chat.chat.message.MessagesList

enum class Screens(val title: String) {
    CHATS("chat"),
    MESSAGES("messages"),
}

@Composable
fun MainContent(
    mainContentViewModel: MainContentViewModel = viewModel()
) {
    val mainContentUiState by mainContentViewModel.mainContentUiState.collectAsState()
    val navHostController = rememberNavController()

    NavHost(
        navController = navHostController,
        startDestination = Screens.CHATS.title
    ) {
        composable(Screens.CHATS.title) {
            ChatsList {
                mainContentViewModel.setMainContent(it.messagesListUiSate)
                navHostController.navigate(Screens.MESSAGES.title)
            }
        }
        composable(Screens.MESSAGES.title) {
            MessagesList(
                modifier = Modifier,
                data = mainContentUiState.data
            ) {
                navHostController.popBackStack(Screens.CHATS.title, inclusive = false)
            }
        }
    }
}
