package by.bashlikovv.chat.app.screens.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.R
import by.bashlikovv.chat.app.views.dropmenu.ChatMenu

private fun getTopChatBarConstraints(): ConstraintSet {
    return ConstraintSet {
        val closeChat = createRefFor("closeChat")
        val userImage = createRefFor("userImage")
        val more = createRefFor("more")
        val chatName = createRefFor("chatName")

        constrain(closeChat) {
            start.linkTo(anchor = parent.start, margin = 5.dp)
            top.linkTo(anchor = parent.top)
            bottom.linkTo(anchor = parent.bottom)
        }
        constrain(userImage) {
            start.linkTo(anchor = closeChat.end, margin = 5.dp)
            bottom.linkTo(anchor = parent.bottom, margin = 5.dp)
            top.linkTo(anchor = parent.top, margin = 5.dp)
        }
        constrain(chatName) {
            start.linkTo(anchor = userImage.end, margin = 5.dp)
            bottom.linkTo(anchor = parent.bottom, margin = 5.dp)
            top.linkTo(anchor = parent.top, margin = 5.dp)
        }
        constrain(more) {
            end.linkTo(anchor = parent.end, margin = 5.dp)
            top.linkTo(anchor = parent.top)
            bottom.linkTo(anchor = parent.bottom)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TopChatBar(chatViewModel: ChatViewModel = viewModel(), onBackAction: () -> Unit) {
    val chatUiState by chatViewModel.chatUiState.collectAsState()
    val dMenuState by chatViewModel.dMenuExpanded.collectAsState()

    BoxWithConstraints {
        ConstraintLayout(
            constraintSet = getTopChatBarConstraints(),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .background(MaterialTheme.colors.primary)
        ) {
            Image(
                painter = painterResource(R.drawable.arrow_back),
                contentDescription = "Close chat",
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colors.secondary),
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        onBackAction()
                    }
                    .layoutId("closeChat")
            )
            Image(
                bitmap = chatUiState.chat.user.userImage.userImageBitmap.asImageBitmap(),
                contentDescription = "User image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .layoutId("userImage")
                    .clip(RoundedCornerShape(25.dp))
                    .size(50.dp)
            )
            Text(
                text = chatUiState.chat.user.userName,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.layoutId("chatName")
            )
            Image(
                painter = painterResource(
                    if (dMenuState)
                        R.drawable.more_horiz
                    else
                        R.drawable.more_vert
                ),
                contentDescription = "More",
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colors.secondary),
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        chatViewModel.onDMenuAction(true)
                    }
                    .layoutId("more")
            )
        }
        ChatMenu()
    }
}