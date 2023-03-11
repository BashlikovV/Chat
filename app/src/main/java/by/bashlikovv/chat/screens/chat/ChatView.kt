package by.bashlikovv.chat.screens.chat

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.struct.Message
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ChatView(modifier: Modifier = Modifier, onBackAction: () -> Unit) {
    Scaffold(topBar = { TopChatBar { onBackAction() } }, bottomBar = { BottomInputFiled() }) {
        ChatContent(modifier = modifier.padding(it).fillMaxSize())
    }
}

@Composable
fun ChatContent(modifier: Modifier = Modifier, chatViewModel: ChatViewModel = viewModel()) {
    val chatUiState by chatViewModel.chatUiState.collectAsState()

    LazyColumn(
        modifier = modifier,
        state = LazyListState(chatUiState.chat.messages.size)
    ) {
        items(chatUiState.chat.messages) {
            MessageView(message = it) { message ->
                chatViewModel.onActionItemClicked(message)
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun MessageView(
    message: Message,
    chatViewModel: ChatViewModel = viewModel(),
    onItemClicked: (Message) -> Unit
) {
    val chatUiState by chatViewModel.chatUiState.collectAsState()

    val textMeasurer = rememberTextMeasurer()
    val measuredText = textMeasurer.measure(
        AnnotatedString(chatViewModel.processText(message.value)),
        style = TextStyle(fontSize = 18.sp, color = MaterialTheme.colors.secondary),
        maxLines = 25,
        overflow = TextOverflow.Ellipsis
    )
    val timeText = textMeasurer.measure(
        text = AnnotatedString(message.time),
        style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.secondary)
    )

    val boxWidth by animateFloatAsState(
        if (message == chatUiState.selectedMessage) {
            (LocalConfiguration.current.screenWidthDp * 2.15).toFloat() + 10f
        } else {
            (LocalConfiguration.current.screenWidthDp * 2.15).toFloat()
        }
    )
    val offset = 5f
    val rectColor= MaterialTheme.colors.primary
    val height = (if(measuredText.lineCount == 1) 28.8 else 24.5)

    Row(
        horizontalArrangement = if (message.user.userName == chatUiState.usersData.first().userName)
            Arrangement.End
        else
            Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .height(
                if (!message.isImage)
                    ((measuredText.lineCount * 24 + offset * 2).dp)
                else
                    (message.imageBitmap.height / 8).dp)
            .clickable { onItemClicked(message) }
            .padding(start = 4.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .width(boxWidth.dp)
                .height(
                    if (!message.isImage)
                        (measuredText.lineCount * height).dp
                    else
                        (message.imageBitmap.height / 8).dp)
        ) {
            if (!message.isImage) {
                drawRoundRect(
                    rectColor,
                    size = Size(
                        width = boxWidth,
                        height = measuredText.size.height + offset * 2,
                    ),
                    cornerRadius = CornerRadius(30f, 30f),
                    alpha = 0.9f
                )
                drawText(
                    textLayoutResult = measuredText,
                    topLeft = Offset(offset, offset)
                )
                drawText(
                    textLayoutResult = timeText,
                    topLeft = Offset(
                        (boxWidth - 90),
                        (measuredText.size.height - (12.sp).toPx())
                    )
                )
            } else {
                var rotation = 0f
                if (message.imageBitmap.width > message.imageBitmap.height) {
                    rotation = 90f
                }
                rotate(degrees = rotation) {
                    drawImage(
                        image = message.imageBitmap.asImageBitmap(),
                        dstSize = IntSize(
                            (message.imageBitmap.width / 3),
                            (message.imageBitmap.height / 3)
                        )
                    )
                }
            }
        }
    }
}