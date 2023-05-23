package by.bashlikovv.chat.app.screens.messenger

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import by.bashlikovv.chat.app.screens.messenger.chats.MessengerContent
import by.bashlikovv.chat.app.screens.messenger.settings.SettingsView
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.views.drawer.MessengerDrawerContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MessengerView(
    modifier: Modifier = Modifier,
    messengerViewModel: MessengerViewModel = viewModel(),
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
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(navController = navHostController, startDestination = Screens.CHATS.name) {
                composable(Screens.CHATS.name) {
                    MessengerContent(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .pullRefresh(state, true)
                    ) { onOpenChat(it) }
                }
                composable(Screens.CONTACTS.name) {
                    MessengerContent(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .pullRefresh(state, true)
                    ) { onOpenChat(it) }
                }
                composable(Screens.SETTINGS.name) {
                    SettingsView()
                }
            }
            PullRefreshIndicator(
                refreshing,
                state,
                Modifier.align(Alignment.TopCenter),
                contentColor = MaterialTheme.colors.onError,
                backgroundColor = MaterialTheme.colors.primary
            )
        }
    }
}