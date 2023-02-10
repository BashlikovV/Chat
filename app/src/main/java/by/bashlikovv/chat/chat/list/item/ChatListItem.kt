package by.bashlikovv.chat.chat.list.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
import by.bashlikovv.chat.R
import by.bashlikovv.chat.chat.list.ChatListUiState
import by.bashlikovv.chat.ui.theme.Teal200

@Composable
fun ChatItem(
    modifier: Modifier = Modifier,
    data: ChatListUiState,
    onChatsItemSelect: (ChatListUiState) -> Unit,
    onLongPress: (Offset) -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .clickable {
                onChatsItemSelect(data)
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onLongPress(it)
                    }
                )
            }
            .fillMaxWidth()
    ) {
        ChatItemContent(data = data)
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
    data: ChatListUiState
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
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
        data = ChatListUiState(
            image = R.drawable.test_face_man,
            name = "Vasy Pupkin",
            displayedMessage = "Please, buy my NFT cart",
            time = "00:00",
            unreadMessagesCount = 3
        ),
        onLongPress = {},
        onChatsItemSelect = {}
    )
}