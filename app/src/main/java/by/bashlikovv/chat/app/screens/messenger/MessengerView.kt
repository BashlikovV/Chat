package by.bashlikovv.chat.app.screens.messenger

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.app.views.drawer.MessengerDrawerContent
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.views.fab.MessengerFABContent

@Composable
fun MessengerView(
    modifier: Modifier = Modifier,
    messengerViewModel: MessengerViewModel = viewModel(),
    onOpenChat: (Chat) -> Unit
) {
    val messengerUiState by messengerViewModel.messengerUiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar() },
        drawerContent = { MessengerDrawerContent() },
        floatingActionButton = { MessengerFABContent() },
        scaffoldState = ScaffoldState(
            drawerState = messengerUiState.drawerState,
            snackbarHostState = SnackbarHostState()
        )
    ) { paddingValues ->
        MessengerContent(modifier = modifier.padding(paddingValues).fillMaxSize()) { onOpenChat(it) }
    }
}

@Composable
fun MessengerContent(
    modifier: Modifier = Modifier,
    messengerViewModel: MessengerViewModel = viewModel(),
    onOpenChat: (Chat) -> Unit
) {
    val messengerUiState by messengerViewModel.messengerUiState.collectAsState()

    LazyColumn(
        modifier = modifier.background(MaterialTheme.colors.primary),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        if (messengerUiState.chats.isNotEmpty()) {
            if (!messengerUiState.expanded) {
                items(messengerUiState.chats) { chat -> MessengerItem(chat) { onOpenChat(it) } }
            } else {
                items(messengerUiState.searchedItems) { chat -> MessengerItem(chat) { onOpenChat(it) } }
            }
        }
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
            top.linkTo(anchor = parent.top, margin = 15.dp)
            bottom.linkTo(anchor = parent.bottom)
        }
    }
}

@Composable
fun MessengerItem(
    chat: Chat,
    modifier: Modifier = Modifier,
    messengerViewModel: MessengerViewModel = viewModel(),
    onOpenChat: (Chat) -> Unit
) {
    BoxWithConstraints {
        ConstraintLayout(
            constraintSet = getMessengerItemConstraints(),
            modifier = modifier.fillMaxWidth().background(messengerViewModel.getChatBackground(chat))
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
            Image(
                bitmap = chat.user.userImage.userImageBitmap.asImageBitmap(),
                contentDescription = "chat with ${chat.user.userName}",
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(color = messengerViewModel.getTintColor(chat)),
                modifier = Modifier.clip(RoundedCornerShape(25.dp)).size(50.dp).layoutId("image")
            )
            MessengerItemText(
                text = chat.user.userName,
                fontWeight = FontWeight.Bold,
                fontSize = 16,
                layoutId = "name",
                textColor = messengerViewModel.getTextColor(chat)
            )
            MessengerItemText(
                text = chat.messages.last().value,
                fontWeight = FontWeight.Light,
                fontSize = 14,
                layoutId = "message",
                textColor = messengerViewModel.getTextColor(chat)
            )
            MessengerItemText(
                text = chat.messages.last().time,
                fontWeight = FontWeight.Thin,
                fontSize = 13,
                layoutId = "time",
                textColor = messengerViewModel.getTextColor(chat)
            )
            MessagesCount(
                count = chat.count, color = messengerViewModel.getTintColor(chat),
                countColor = messengerViewModel.getCountColor(chat), modifier = Modifier.layoutId("count")
            )
        }
    }
}

@Composable
fun MessengerItemText(
    text: String,
    fontWeight: FontWeight,
    fontSize: Int,
    textColor: Color,
    layoutId: String
) {
    Text(
        text = text,
        fontWeight = fontWeight,
        fontSize = fontSize.sp,
        maxLines = 1,
        overflow = TextOverflow.Clip,
        modifier = Modifier.layoutId(layoutId),
        color = textColor
    )
}

@Composable
fun MessagesCount(
    count: Int,
    color: Color,
    countColor: Color,
    modifier: Modifier
) {
    if (count != 0) {
        Text(
            text = "$count",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            modifier = modifier.clip(RoundedCornerShape(25.dp)).background(color).padding(
                    horizontal = 7.5.dp, vertical = 2.dp
                ),
            color = countColor
        )
    }
}