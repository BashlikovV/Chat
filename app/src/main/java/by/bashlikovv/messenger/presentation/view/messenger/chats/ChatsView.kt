package by.bashlikovv.messenger.presentation.view.messenger.chats

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import by.bashlikovv.messenger.R
import by.bashlikovv.messenger.domain.model.Chat
import by.bashlikovv.messenger.presentation.viewmodel.MessengerViewModel
import by.bashlikovv.messenger.utils.buildTime
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@Composable
fun ChatsView(
    modifier: Modifier = Modifier,
    messengerViewModel: MessengerViewModel = koinViewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    ),
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
                items(messengerUiState.chats) { chat -> ChatItem(chat) { onOpenChat(it) } }
            } else {
                items(searchedItems) { chat -> ChatItem(chat) { onOpenChat(it) } }
            }
        }
    }
}

@Composable
fun UserImageView(image: String, username: String) {
    AsyncImage(
        model = image,
        contentDescription = "chat with $username",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(16.dp))
    )
}

@Composable
fun UserNameView(username: String, modifier: Modifier = Modifier) {
    Text(
        text = username,
        color = MaterialTheme.colors.onSurface,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        modifier = modifier.height(24.dp)
    )
}

@Composable
fun LastMessageView(lastMessage: String, modifier: Modifier = Modifier) {
    Text(
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
    Text(
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
        Text(
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
private fun AvatarBadge(chat: Chat) {
    if (Calendar.getInstance().time.time - chat.user.lastConnectionTime.time < 300000) {
        Icon(
            painter = painterResource(id = R.drawable.avatar_badge),
            contentDescription = "Alive",
            tint = MaterialTheme.colors.onError,
            modifier = Modifier.size(10.dp)
        )
    } else {
        Icon(
            painter = painterResource(id = R.drawable.avatar_unfilled_badge),
            contentDescription = "Alive",
            tint = MaterialTheme.colors.onError,
            modifier = Modifier.size(10.dp)
        )
    }
}

@Composable
fun ChatItem(
    chat: Chat,
    modifier: Modifier = Modifier,
    messengerViewModel: MessengerViewModel = koinViewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    ),
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
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.BottomEnd
            ) {
                UserImageView(
                    chat.user.userImage,
                    username = chat.user.userName
                )
                AvatarBadge(chat)
            }
        }
        Column(modifier = Modifier.fillMaxSize()) {
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