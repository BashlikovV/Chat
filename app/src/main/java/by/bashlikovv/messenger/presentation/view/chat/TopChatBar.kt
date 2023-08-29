package by.bashlikovv.messenger.presentation.view.chat

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import by.bashlikovv.messenger.R
import by.bashlikovv.messenger.presentation.viewmodel.ChatViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar
import java.util.Date

@Composable
fun TopChatBar(chatViewModel: ChatViewModel = koinViewModel(), onBackAction: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colors.primary)
    ) {
        Column(
            modifier = Modifier.weight(0.7f),
            verticalArrangement = Arrangement.Center
        ) {
            TopBarLeftContent(chatViewModel) { onBackAction() }
        }
        Column(modifier = Modifier.weight(0.3f)) {
            TopBarRightContent()
        }
    }
}

@Composable
private fun TopBarLeftContent(
    chatViewModel: ChatViewModel = koinViewModel(),
    onBackAction: () -> Unit
) {
    val chatUiState by chatViewModel.chatUiState.collectAsState()
    val time = Calendar.getInstance().time.time - chatUiState.chat.user.lastConnectionTime.time
    val activityText = if (time < 300000) {
        "Online"
    } else {
        Date(time).toString().substringBefore("GMT")
    }

    Row(
        modifier = Modifier
            .padding(start = 20.dp)
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            painter = painterResource(id = R.drawable.arrow_back),
            contentDescription = "back",
            tint = MaterialTheme.colors.onError,
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackAction() }
        )
        ChatImageView()
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            androidx.compose.material3.Text(
                text = chatUiState.chat.user.userName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.colors.onSurface
            )
            androidx.compose.material3.Text(
                text = activityText,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = MaterialTheme.colors.surface
            )
        }
    }
}

@Composable
private fun ChatImageView(
    chatViewModel: ChatViewModel = koinViewModel()
) {
    val chatUiState by chatViewModel.chatUiState.collectAsState()

    Box(
        modifier = Modifier.padding(start = 27.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Image(
            bitmap = chatUiState.chat.user.userImage.userImageBitmap.asImageBitmap(),
            contentDescription = "Icon",
            modifier = Modifier.size(40.dp)
        )
        if (Calendar.getInstance().time.time - chatUiState.chat.user.lastConnectionTime.time < 300000) {
            ActivityIcon(icon = R.drawable.avatar_badge, isActive = true)
        } else {
            ActivityIcon(icon = R.drawable.avatar_unfilled_badge, isActive = false)
        }
    }
}

@Composable
private fun ActivityIcon(@DrawableRes icon: Int, isActive: Boolean) {
    Icon(
        painter = painterResource(id = icon),
        contentDescription = if (isActive) "Online" else "Offline",
        tint = MaterialTheme.colors.onError,
        modifier = Modifier.size(10.dp)
    )
}

@Composable
private fun TopBarRightContent(chatViewModel: ChatViewModel = koinViewModel()) {
    val selectedItems by chatViewModel.selectedItemsState.collectAsState()

    Row(
        modifier = Modifier
            .padding(end = 20.dp)
            .fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(30.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(visible = selectedItems.containsValue(true)) {
            TopBarRightIcon(icon = R.drawable.delete_outline, description = "delete selected messages") {
                chatViewModel.onActionDelete()
                chatViewModel.clearSelectedMessages()
            }
        }
        AnimatedVisibility(visible = !selectedItems.containsValue(true)) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(30.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TopBarRightIcon(R.drawable.call, "call") {  }
                TopBarRightIcon(R.drawable.video, "video call") {  }
            }
        }
    }
}

@Composable
private fun TopBarRightIcon(
    @DrawableRes icon: Int,
    description: String,
    onClick: () -> Unit
) {
    Icon(
        painter = painterResource(id = icon),
        contentDescription = description,
        tint = MaterialTheme.colors.onError,
        modifier = Modifier
            .size(24.dp)
            .clickable { onClick() }
    )
}