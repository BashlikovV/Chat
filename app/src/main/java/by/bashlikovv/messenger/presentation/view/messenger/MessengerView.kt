package by.bashlikovv.messenger.presentation.view.messenger

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import by.bashlikovv.messenger.domain.model.Chat
import by.bashlikovv.messenger.presentation.view.messenger.chats.ChatsView
import by.bashlikovv.messenger.presentation.view.messenger.settings.SettingsView
import by.bashlikovv.messenger.presentation.view.messenger.users.UsersView
import by.bashlikovv.messenger.presentation.viewmodel.MessengerViewModel
import by.bashlikovv.messenger.presentation.views.drawer.MessengerDrawerContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MessengerView(
    modifier: Modifier = Modifier,
    messengerViewModel: MessengerViewModel = koinViewModel(),
    navHostController: NavHostController,
    onOpenChat: (Chat) -> Unit
) {
    val drawerState by messengerViewModel.drawerState.collectAsState()

    val scope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    fun refresh () = scope.launch(Dispatchers.IO) {
        refreshing = true
        messengerViewModel.loadViewData()
        delay(500)
        refreshing = false
    }
    val state = rememberPullRefreshState(refreshing, ::refresh)

    Scaffold(
        topBar = { TopAppBar() },
        drawerContent = { MessengerDrawerContent() },
        bottomBar = { MessengerBottomNavigationBar(navHostController) },
        scaffoldState = ScaffoldState(
            drawerState = drawerState,
            snackbarHostState = SnackbarHostState()
        ),
        modifier = modifier
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            MessengerNavHost(navHostController, state, onOpenChat)
            PullRefreshIndicator(
                refreshing,
                state,
                Modifier.align(androidx.compose.ui.Alignment.Companion.TopCenter),
                contentColor = MaterialTheme.colors.onError,
                backgroundColor = MaterialTheme.colors.primary
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun MessengerNavHost(
    navHostController: NavHostController,
    state: PullRefreshState,
    onOpenChat: (Chat) -> Unit
) {
    NavHost(navController = navHostController, startDestination = Screens.CHATS.name) {
        composable(Screens.CHATS.name) {
            ChatsView(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(state, true)
            ) { onOpenChat(it) }
        }
        composable(Screens.CONTACTS.name) {
            UsersView(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(state, true)
            ) { onOpenChat(it) }
        }
        composable(Screens.SETTINGS.name) {
            SettingsView()
        }
    }
}