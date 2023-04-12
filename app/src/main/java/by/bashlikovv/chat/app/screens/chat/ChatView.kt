package by.bashlikovv.chat.app.screens.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.R
import by.bashlikovv.chat.app.struct.Message
import by.bashlikovv.chat.app.utils.buildTime
import by.bashlikovv.chat.app.utils.dpToFloat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ChatView(modifier: Modifier = Modifier, onBackAction: () -> Unit) {
    Scaffold(topBar = { TopChatBar { onBackAction() } }, bottomBar = { BottomInputFiled() }) {
        ChatContent(modifier = modifier.padding(it).fillMaxSize())
    }
}

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ChatContent(modifier: Modifier = Modifier, chatViewModel: ChatViewModel = viewModel()) {
    val chatUiState by chatViewModel.chatUiState.collectAsState()
    val lazyListState by chatViewModel.lazyListState.collectAsState()

    LaunchedEffect(Unit) {
        chatViewModel.startWork()
    }
    DisposableEffect(Unit) {
        DisposableEffectScope().onDispose {
            chatViewModel.cancelWork()
        }
    }
    val scope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    fun refresh() = scope.launch(Dispatchers.IO) {
        refreshing = true
        chatViewModel.onActionRefresh()
        delay(500)
        refreshing = false
    }
    val state = rememberPullRefreshState(refreshing, ::refresh)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .pullRefresh(state, true),
            state = lazyListState
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Pull up to load messages",
                        style = TextStyle(fontSize = 19.sp, color = MaterialTheme.colors.secondary)
                    )
                }
            }
            items(chatUiState.chat.messages) {
                MessageView(message = it) { message ->
                    chatViewModel.onActionItemClicked(message)
                }
            }
        }
        PullRefreshIndicator(
            refreshing, state, Modifier.align(Alignment.TopCenter), contentColor = MaterialTheme.colors.secondary
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalTextApi::class)
@Composable
fun MessageView(
    message: Message,
    chatViewModel: ChatViewModel = viewModel(),
    onItemClicked: (Message) -> Unit
) {
    val chatUiState by chatViewModel.chatUiState.collectAsState()
    val resources = LocalContext.current.resources

    val textMeasurer = rememberTextMeasurer()
    val boxWidth by animateFloatAsState(
        if (message == chatUiState.selectedMessage) {
            (LocalConfiguration.current.screenWidthDp * 2.15).toFloat() + 10f
        } else {
            (LocalConfiguration.current.screenWidthDp * 2.15).toFloat()
        }
    )

    val measuredText = textMeasurer.measure(
        AnnotatedString(chatViewModel.processText(message.value)),
        style = TextStyle(fontSize = 19.sp, color = MaterialTheme.colors.secondary),
        maxLines = 25,
        overflow = TextOverflow.Ellipsis
    )

    val timeText = textMeasurer.measure(
        text = AnnotatedString(buildTime(message.time)),
        style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.secondary)
    )

    val rectColor = MaterialTheme.colors.primary
    val height = (if(measuredText.lineCount == 1) 28.8 else 24.5)

    Row(
        horizontalArrangement = if (message.from != chatUiState.chat.user.userToken)
            Arrangement.End
        else
            Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .height(
                if (!message.isImage)
                    ((measuredText.lineCount * 24 + 5f * 2).dp)
                else
                    (message.imageBitmap.height / 8).dp)
            .padding(start = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (clip, canvas) = createRefs()

            if (chatViewModel.messageCheapVisible[chatUiState.chat.messages.indexOf(message)]) {
                ClipDelete(
                    modifier = Modifier
                        .constrainAs(clip) {
                            if (message.from != chatUiState.chat.user.userToken) {
                                end.linkTo(anchor = canvas.start, margin = 5.dp)
                            } else {
                                start.linkTo(anchor = canvas.end, margin = 5.dp)
                            }
                        }
                        .fillMaxWidth(0.1f)
                        .height((measuredText.lineCount * height).dp)
                ) {
                    chatViewModel.onActionDelete(message)
                }
            }
            if (!message.isImage) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .width(boxWidth.dp)
                        .clickable {
                            onItemClicked(message)
                            chatViewModel.onCheapItemClicked(
                                message,
                                !chatViewModel.messageCheapVisible[chatViewModel.getMessageIndex(message)]
                            )
                        }
                        .height(
                            if (!message.isImage)
                                (measuredText.lineCount * height).dp
                            else
                                (message.imageBitmap.height / 10).dp)
                        .constrainAs(canvas) {
                            if (message.from != chatUiState.chat.user.userToken) {
                                end.linkTo(anchor = parent.end)
                            } else {
                                start.linkTo(anchor = parent.start)
                            }
                        }
                ) {
                    drawRoundRect(
                        rectColor,
                        size = Size(
                            width = boxWidth,
                            height = (measuredText.lineCount * height).toFloat().dpToFloat(resources = resources),
                        ),
                        cornerRadius = CornerRadius(30f, 30f),
                        alpha = 0.9f
                    )
                    drawText(
                        textLayoutResult = measuredText,
                        topLeft = Offset(10f, 5f)
                    )
                    drawText(
                        textLayoutResult = timeText,
                        topLeft = Offset(
                            (boxWidth - 90),
                            (measuredText.size.height - (12.sp).toPx())
                        )
                    )
                }
            } else {
                Image(
                    bitmap = message.imageBitmap.asImageBitmap(),
                    contentDescription = "image",
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .clickable {
                            onItemClicked(message)
                            chatViewModel.onCheapItemClicked(
                                message,
                                !chatViewModel.messageCheapVisible[chatViewModel.getMessageIndex(message)]
                            )
                        }
                        .constrainAs(canvas) {
                            if (message.from != chatUiState.chat.user.userToken) {
                                end.linkTo(anchor = parent.end)
                            } else {
                                start.linkTo(anchor = parent.start)
                            }
                        }
                        .padding(5.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClipDelete(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedAssistChip(
        onClick = {
            onClick()
        },
        label = {  },
        leadingIcon = {
            Image(
                painter = painterResource(R.drawable.delete_outline),
                contentDescription = "Delete message",
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary)
            )
        },
        enabled = true,
        modifier = modifier,
        colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colors.primary,
            leadingIconContentColor = MaterialTheme.colors.secondary
        )
    )
}