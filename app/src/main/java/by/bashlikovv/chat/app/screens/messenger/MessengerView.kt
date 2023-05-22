package by.bashlikovv.chat.app.screens.messenger

import android.graphics.Bitmap
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import by.bashlikovv.chat.R
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.utils.buildTime
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
                    Text(text = "settings")
                }
            }
            PullRefreshIndicator(
                refreshing, state, Modifier.align(Alignment.TopCenter), contentColor = MaterialTheme.colors.primary
            )
        }
    }
}

@Composable
fun MessengerContent(
    modifier: Modifier = Modifier,
    messengerViewModel: MessengerViewModel = viewModel(LocalContext.current as ComponentActivity),
    onOpenChat: (Chat) -> Unit
) {
    val messengerUiState by messengerViewModel.messengerUiState.collectAsState()
    val searchedItems by messengerViewModel.searchedItems.collectAsState()

    LazyColumn(
        modifier = modifier.background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (messengerUiState.chats.isNotEmpty()) {
            if (!messengerUiState.expanded) {
                items(messengerUiState.chats) { chat -> MessengerItem(chat) { onOpenChat(it) } }
            } else {
                items(searchedItems) { chat -> MessengerItem(chat) { onOpenChat(it) } }
            }
        }
    }
}

@Composable
fun UserImageView(image: Bitmap, username: String) {
    Image(
        bitmap = image.asImageBitmap(),
        contentDescription = "chat with $username",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(16.dp))
    )
}

@Composable
fun UserNameView(username: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(
        text = username,
        color = MaterialTheme.colors.onSurface,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        modifier = modifier.height(24.dp)
    )
}

@Composable
fun LastMessageView(lastMessage: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(
        text = lastMessage,
        color = MaterialTheme.colors.surface,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        fontSize = 11.sp,
        modifier = modifier.height(20.dp),
        overflow = TextOverflow.Clip
    )
}

@Composable
fun LastMessageTimeView(time: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(
        text = time,
        color = MaterialTheme.colors.onSurface,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        modifier = modifier
    )
}

@Composable
fun MessagesCountView(count: Int, modifier: Modifier = Modifier) {
    if (count > 0) {
        androidx.compose.material3.Text(
            text = count.toString(),
            color = MaterialTheme.colors.onSurface,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 10.sp,
            textAlign = TextAlign.Center,
            modifier = modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colors.onError)
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.zero),
            contentDescription = "no unread messages",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun MessengerItem(
    chat: Chat,
    modifier: Modifier = Modifier,
    messengerViewModel: MessengerViewModel = viewModel(LocalContext.current as ComponentActivity),
    onOpenChat: (Chat) -> Unit
) {
    val messengerUiState by messengerViewModel.messengerUiState.collectAsState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 10.dp)
            .background(
                if (messengerUiState.selectedItem == chat) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.background
                }
            )
            .pointerInput(chat) {
                detectTapGestures(
                    onLongPress = { messengerViewModel.onActionSelect(chat) },
                    onTap = {
                        messengerViewModel.onActionOpenChat(chat)
                        onOpenChat(chat)
                    }
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 12.dp)
        ) {
            UserImageView(
                chat.user.userImage.userImageBitmap,
                username = chat.user.userName
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                UserNameView(
                    username = chat.user.userName,
                    modifier = Modifier.weight(0.9f)
                )
                LastMessageTimeView(
                    time = buildTime(chat.messages.last().time),
                    modifier = Modifier.weight(0.1f)
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                LastMessageView(
                    lastMessage = if (chat.messages.last().value.isNotEmpty()) {
                        "${chat.messages.last().user.userName}: ${chat.messages.last().value}"
                    } else {
                        ""
                    },
                    modifier = Modifier.weight(0.9f)
                )
                MessagesCountView(
                    count = chat.count,
                    modifier = Modifier.weight(0.1f)
                )
            }
        }
    }
}