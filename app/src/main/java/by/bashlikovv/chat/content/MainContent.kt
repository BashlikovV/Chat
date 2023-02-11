package by.bashlikovv.chat.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import by.bashlikovv.chat.chat.list.ChatsList
import by.bashlikovv.chat.chat.message.MessagesList
import by.bashlikovv.chat.nav.DrawerViewModel

/**
 * [Screens] -> enum with screens of application
 * [MainContent] -> NavHost wit screens of application
 * * [ChatsList] -> List of chats of current application user
 * * [MessagesList] -> List of messages between current app user and his companion
 * * * [MainContentViewModel] contains list with messages and this list getting every
 * * * time whet user clicked in chat (navigateToMessage callback) in [ChatsList]
 * */

enum class Screens(val title: String) {
    CHATS("chat"),
    MESSAGES("messages"),
}

@Composable
fun MainContent(
    mainContentViewModel: MainContentViewModel = viewModel(),
    drawerViewModel: DrawerViewModel = viewModel()
) {
    val navHostController = rememberNavController()

    NavHost(
        navController = navHostController,
        startDestination = Screens.CHATS.title
    ) {
        composable(Screens.CHATS.title) {
            ChatsList(drawerViewModel = drawerViewModel) {
                mainContentViewModel.setMainContent(it.messagesListUiSate)
                navHostController.navigate(Screens.MESSAGES.title)
            }
        }
        composable(Screens.MESSAGES.title) {
            MessagesList(
                modifier = Modifier,
                mainContentViewModel = mainContentViewModel
            ) {
                navHostController.popBackStack(Screens.CHATS.title, inclusive = false)
            }
        }
    }
}
