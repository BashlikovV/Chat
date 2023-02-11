package by.bashlikovv.chat.chat.list.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.chat.list.ChatListUiState
import by.bashlikovv.chat.chat.list.ChatListViewModel
import by.bashlikovv.chat.ui.theme.Teal200

@Composable
fun ChatItem(
    modifier: Modifier = Modifier,
    chatListViewModel: ChatListViewModel = viewModel(),
    index: Int,
    onChatsItemSelect: (ChatListUiState) -> Unit,
    onLongPress: (ChatListUiState) -> Unit
) {
    val chatListUiState by chatListViewModel.chatListUiState.collectAsState()

    Card(
        modifier = modifier
            .fillMaxWidth()
    ) {
        ChatItemContent(
            data = chatListUiState[index],
            onChatsItemSelect =  { onChatsItemSelect(it) },
            onLongPress = { onLongPress(chatListUiState[index]) }
        )
    }
}

private fun getChatItemConstraints(): ConstraintSet {
    return ConstraintSet {
        val image = createRefFor("image")
        val name = createRefFor("name")
        val message = createRefFor("message")
        val time = createRefFor("time")
        val count = createRefFor("count")

        constrain(image) {
            top.linkTo(anchor = parent.top, margin = 5.dp)
            bottom.linkTo(anchor = parent.bottom, margin = 5.dp)
            start.linkTo(anchor = parent.start, margin = 5.dp)
        }
        constrain(name) {
            top.linkTo(anchor = parent.top, margin = 5.dp)
            bottom.linkTo(anchor = message.top)
            start.linkTo(anchor = image.end, margin = 5.dp)
        }
        constrain(message) {
            top.linkTo(anchor = name.bottom)
            bottom.linkTo(anchor = parent.bottom, margin = 5.dp)
            start.linkTo(anchor = image.end, margin = 5.dp)
        }
        constrain(time) {
            top.linkTo(anchor = parent.top, margin = 5.dp)
            end.linkTo(anchor = parent.end, margin = 12.dp)
        }
        constrain(count) {
            end.linkTo(anchor = parent.end, margin = 10.dp)
            top.linkTo(anchor = parent.top)
            bottom.linkTo(anchor = parent.bottom)
        }
    }
}

@Composable
fun ChatItemContent(
    modifier: Modifier = Modifier,
    data: ChatListUiState,
    onChatsItemSelect: (ChatListUiState) -> Unit,
    onLongPress: (ChatListUiState) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress(data) },
                    onTap = { onChatsItemSelect(data) }
                )
            }
    ) {
        val constraints = getChatItemConstraints()

        ConstraintLayout(
            modifier = Modifier.fillMaxWidth(),
            constraintSet = constraints
        ) {
            Image(
                painter = painterResource(data.image),
                contentDescription = "chat with ${data.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .layoutId("image")
            )
            Text(
                text = data.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier.layoutId("name")
            )
            Text(
                text = data.displayedMessage,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier.layoutId("message")
            )
            Text(
                text = data.time,
                fontWeight = FontWeight.Thin,
                fontSize = 7.sp,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                modifier = Modifier.layoutId("time")
            )
            MessagesCount(
                count = data.unreadMessagesCount,
                modifier = Modifier.layoutId("count")
            )
        }
    }
}

@Composable
fun MessagesCount(
    modifier: Modifier = Modifier,
    count: Int
) {
    if (count != 0) {
        Text(
            text = "$count",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            modifier = modifier
                .clip(RoundedCornerShape(25.dp))
                .background(Teal200)
                .padding(
                    horizontal = 7.5.dp,
                    vertical = 2.dp
                )
        )
    }
}

@Preview
@Composable
fun ChatItemPreview() {
    ChatItem(
        index = 0,
        onLongPress = {},
        onChatsItemSelect = {}
    )
}