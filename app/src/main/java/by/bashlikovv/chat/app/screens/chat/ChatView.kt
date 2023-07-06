package by.bashlikovv.chat.app.screens.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.R
import by.bashlikovv.chat.app.struct.Message
import by.bashlikovv.chat.app.theme.MessageShape
import by.bashlikovv.chat.app.utils.buildTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ChatView(onBackAction: () -> Unit) {
    Scaffold(
        topBar = { TopChatBar { onBackAction() } },
        bottomBar = { BottomChatBar() }
    ) {
        ChatContent(modifier = Modifier
            .padding(it)
            .fillMaxSize())
    }
}

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ChatContent(modifier: Modifier = Modifier, chatViewModel: ChatViewModel = viewModel()) {
    val chatUiState by chatViewModel.chatUiState.collectAsState()
    val lazyListState by chatViewModel.lazyListState.collectAsState()
    val selectedItemsState by chatViewModel.selectedItemsState.collectAsState()
    val scope = rememberCoroutineScope()

    WorkStarter(chatUiState, chatViewModel)
    var prevDate = try {
        chatUiState.chat.messages.first().time.subSequence(0, 9)
    } catch (_: Exception) {
        ""
    }
    var refreshing by remember { mutableStateOf(false) }
    fun refresh() = scope.launch(Dispatchers.IO) {
        refreshing = true
        refreshing = suspendCancellableCoroutine {
            chatViewModel.viewModelScope.launch(Dispatchers.IO) {
                chatViewModel.onActionRefresh()
                delay(500)
                it.resumeWith(Result.success(false))
            }
        }
    }
    val state = rememberPullRefreshState(refreshing, ::refresh)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .pullRefresh(state, true),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (chatUiState.chat.messages.isEmpty()) {
                item { RowCenteredText(text = "You do not have messages now") }
            } else {
                item { RowCenteredText(text = "Pull up to load messages") }
                items(chatUiState.chat.messages) {
                    if (it.time.isNotEmpty() && it.time.subSequence(0, 9) != prevDate) {
                        prevDate = it.time.subSequence(0, 9)
                        DateSeparator(it)
                    }
                    ChatItem(message = it, selected = selectedItemsState[it] ?: false) { selectedMessage ->
                        chatViewModel.selectMessage(selectedMessage)
                    }
                }
            }
        }
        PullRefreshIndicator(
            refreshing, state, Modifier.align(Alignment.TopCenter),
            contentColor = MaterialTheme.colors.secondary
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun WorkStarter(
    chatUiState: ChatUiState,
    chatViewModel: ChatViewModel
) {
    if (chatUiState.chat.user.userName != "Bookmarks") {
        LaunchedEffect(Unit) {
            chatViewModel.startWork()
        }
        DisposableEffect(Unit) {
            DisposableEffectScope().onDispose {
                chatViewModel.cancelWork()
            }
        }
    }
}

@Composable
fun RowCenteredText(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            style = TextStyle(fontSize = 19.sp, color = MaterialTheme.colors.surface)
        )
    }
}

@Composable
fun DateSeparator(it: Message) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .weight(0.4f)
                .height(1.dp)
                .background(MaterialTheme.colors.surface)
        )
        Text(
            text = it.time.substringBefore(buildTime(it.time)),
            modifier = Modifier.weight(0.2f),
            fontWeight = FontWeight.W100,
            color = MaterialTheme.colors.surface
        )
        Spacer(
            modifier = Modifier
                .weight(0.4f)
                .height(1.dp)
                .background(MaterialTheme.colors.surface)
        )
    }
}

@Composable
private fun ChatItem(
    message: Message,
    chatViewModel: ChatViewModel = viewModel(),
    selected: Boolean,
    onSelect: (message: Message) -> Unit
) {
    val chatUiState by chatViewModel.chatUiState.collectAsState()
    val horizontalArrangement = if (message.from == chatUiState.chat.user.userToken) {
        Arrangement.End
    } else {
        Arrangement.Start
    }

    Row(
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(message) {
                detectTapGestures(
                    onLongPress = { onSelect(message) }
                )
            }
    ) {
        MessageView(message, selected, horizontalArrangement)
    }
}

@Composable
private fun MessageView(
    message: Message,
    selected: Boolean,
    arrangement: Arrangement.Horizontal,
    chatViewModel: ChatViewModel = viewModel()
) {
    val pv = if (arrangement == Arrangement.End) {
        PaddingValues(end = 10.dp)
    } else {
        PaddingValues(start = 10.dp)
    }
    val backgroundColor = if (selected) {
        MaterialTheme.colors.secondary
    } else {
        MaterialTheme.colors.primary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .clip(
                MessageShape(
                    alignment = arrangement == Arrangement.End,
                    radius = 35.dp
                )
            )
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (message.isImage) {
                MessageImageView(message)
            } else {
                androidx.compose.material3.Text(
                    text = chatViewModel.processText(message.value),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(
                        vertical = 2.dp,
                        horizontal = 5.dp
                    ),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colors.surface
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                MessageTimeText(buildTime(message.time))
                MessageReadIcon(message.isRead)
            }
        }
    }
}

@Composable
private fun MessageImageView(message: Message) {
    var scale by remember { mutableStateOf(1f) }
    var rotationState by remember { mutableStateOf(0f) }

    Image(
        bitmap = message.imageBitmap.asImageBitmap(),
        contentDescription = message.value,
        modifier = Modifier
            .pointerInput(message) {
                detectTransformGestures(
                    onGesture = { _: Offset, _: Offset, zoom: Float, rotation: Float ->
                        scale *= zoom
                        rotationState += rotation
                    },
                    panZoomLock = true
                )
            }
            .graphicsLayer(
                scaleX = maxOf(.1f, minOf(3f, scale)),
                scaleY = maxOf(.1f, minOf(3f, scale)),
                rotationZ = rotationState
            )
    )
}

@Composable
private fun MessageReadIcon(isRead: Boolean) {
    if (isRead) {
        Icon(
            painter = painterResource(id = R.drawable.readed),
            contentDescription = "message is read",
            modifier = Modifier.size(11.dp),
            tint = MaterialTheme.colors.onError
        )
    } else {
        Icon(
            painter = painterResource(id = R.drawable.zero),
            contentDescription = "message is read",
            modifier = Modifier.size(11.dp),
            tint = MaterialTheme.colors.onError
        )
    }
}

@Composable
private fun MessageTimeText(time: String) {
    val timeTextStyle = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colors.surface,
        textAlign = TextAlign.End
    )

    Text(
        text = time,
        modifier = Modifier.padding(
            vertical = 2.dp,
            horizontal = 5.dp
        ),
        style = timeTextStyle,
        maxLines = 1
    )
}