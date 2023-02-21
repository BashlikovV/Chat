package by.bashlikovv.chat.messenger

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.drawer.DrawerContent
import by.bashlikovv.chat.model.MessengerTestData
import by.bashlikovv.chat.model.MessengerUiState
import by.bashlikovv.chat.struct.Chat
import by.bashlikovv.chat.theme.PrimaryLight

@Composable
fun MessengerView(modifier: Modifier = Modifier, messengerViewModel: MessengerViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        messengerViewModel.applyMessengerUiState(MessengerUiState(MessengerTestData.testData))
    }

    val messengerUiState by messengerViewModel.messengerUiState.collectAsState()

    ModalDrawer(
        gesturesEnabled = true,
        drawerState = messengerUiState.drawerState,
        drawerBackgroundColor = MaterialTheme.colors.primary,
        drawerContentColor = MaterialTheme.colors.primary,
        drawerContent = {
            DrawerContent()
        }
    ) {
        Scaffold(topBar = { TopAppBar() }) {
            MessengerContent(modifier = modifier.padding(it).fillMaxSize())
        }
    }
}

@Composable
fun MessengerContent(
    modifier: Modifier = Modifier, messengerViewModel: MessengerViewModel = viewModel()
) {
    val messengerUiState by messengerViewModel.messengerUiState.collectAsState()

    LazyColumn(
        modifier = modifier.background(MaterialTheme.colors.primary),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        items(messengerUiState.chats) { MessengerItem(it) }
    }
}

private fun getMessengerItemConstraints(): ConstraintSet {
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
fun MessengerItem(chat: Chat, messengerViewModel: MessengerViewModel = viewModel()) {
    val messengerUiState by messengerViewModel.messengerUiState.collectAsState()

    Card(modifier = Modifier.fillMaxWidth()) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val constraints = getMessengerItemConstraints()

            ConstraintLayout(
                modifier = Modifier.fillMaxWidth().background(
                    if (chat.user.userId == messengerUiState.selectedItem.user.userId) {
                        MaterialTheme.colors.primary
                    } else {
                        MaterialTheme.colors.background
                    }
                ).pointerInput(chat) {
                    detectTapGestures(
                        onLongPress = { messengerViewModel.onActionSelect(chat) },
                        onTap = { messengerViewModel.onActionOpenChat(chat) }
                    )
                }, constraintSet = constraints
            ) {
                Image(
                    painter = painterResource(chat.user.userImage),
                    contentDescription = "chat with ${chat.user.userName}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(50.dp).layoutId("image")
                )
                Text(
                    text = chat.user.userName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.layoutId("name"),
                    color = messengerViewModel.getTextColor(chat)
                )
                Text(
                    text = chat.messages[messengerUiState.chats.indexOf(chat)].value,
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.layoutId("message"),
                    color = messengerViewModel.getTextColor(chat)
                )
                Text(
                    text = chat.messages.last().time,
                    fontWeight = FontWeight.Thin,
                    fontSize = 7.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Visible,
                    modifier = Modifier.layoutId("time"),
                    color = messengerViewModel.getTextColor(chat)
                )
                MessagesCount(
                    count = chat.count, modifier = Modifier.layoutId("count")
                )
            }
        }
    }
}

@Composable
fun MessagesCount(count: Int, modifier: Modifier) {
    if (count != 0) {
        Text(
            text = "$count",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            modifier = modifier.clip(RoundedCornerShape(25.dp)).background(MaterialTheme.colors.primary).padding(
                    horizontal = 7.5.dp, vertical = 2.dp
                ),
            color = PrimaryLight
        )
    }
}