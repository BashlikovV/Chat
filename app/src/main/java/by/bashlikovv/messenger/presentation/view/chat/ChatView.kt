package by.bashlikovv.messenger.presentation.view.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.DisposableEffectScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import by.bashlikovv.messenger.R
import by.bashlikovv.messenger.domain.model.Message
import by.bashlikovv.messenger.presentation.view.theme.MessageShape
import by.bashlikovv.messenger.presentation.viewmodel.ChatViewModel
import by.bashlikovv.messenger.utils.buildTime
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.androidx.compose.koinViewModel

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
fun ChatContent(modifier: Modifier = Modifier, chatViewModel: ChatViewModel = koinViewModel()) {
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
    chatViewModel: ChatViewModel = koinViewModel(),
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
    chatViewModel: ChatViewModel = koinViewModel()
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
                Text(
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

    var scaleState by remember { mutableFloatStateOf(1f) }
    var translationState by remember { mutableStateOf(Offset(0f, 0f)) }
    val state = rememberTransformableState { zoomChange, panChange, _ ->
        scaleState *= zoomChange
        translationState += panChange
    }

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(message.imageBitmap)
            .crossfade(true)
            .build(),
        contentDescription = message.value,
        modifier = Modifier
            .transformable(state)
            .graphicsLayer(
                scaleX = maxOf(0.9f, minOf(3f, scaleState)),
                scaleY = maxOf(0.9f, minOf(3f, scaleState)),
                translationX = translationState.x,
                translationY = translationState.y
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