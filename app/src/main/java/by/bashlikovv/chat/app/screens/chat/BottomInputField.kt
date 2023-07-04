package by.bashlikovv.chat.app.screens.chat

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.R

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomChatBar(chatViewModel: ChatViewModel = viewModel()) {
    val chatUiState by chatViewModel.chatUiState.collectAsState()
    val context = LocalContext.current
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            chatViewModel.applyImageUri(context = context, imageUri = it ?: Uri.EMPTY)
        }
    )
    var iconState by rememberSaveable { mutableStateOf(R.drawable.micro) }
    iconState = if (chatUiState.isCanSend) R.drawable.send else  R.drawable.micro
    val iconAnimation by animateIntAsState(targetValue = iconState, label = "")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colors.primary),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Row(modifier = Modifier
            .padding(start = 5.dp)
            .weight(0.1f)) {
            BottomBarIcon(R.drawable.emoji, "choose emoji") {  }
        }
        BottomBarInputField(modifier = Modifier.weight(0.7f))
        Row(
            modifier = Modifier
                .padding(end = 5.dp)
                .weight(0.2f),
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.End)
        ) {
            BottomBarIcon(R.drawable.folder_open, "add file") {
                chatViewModel.onActionGallery(cameraLauncher)
            }
            BottomBarIcon(iconAnimation, "send voice message") {
                if (chatUiState.isCanSend) {
                    chatViewModel.onActionSend()
                } else {
                    TODO()
                }
            }
        }
    }
}

@Composable
private fun BottomBarIcon(
    @DrawableRes icon: Int,
    description: String,
    onClick: () -> Unit
) {
    Icon(
        painter = painterResource(id = icon),
        contentDescription = description,
        tint = MaterialTheme.colors.onError,
        modifier = Modifier
            .size(26.dp)
            .clickable { onClick() }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun BottomBarInputField(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel = viewModel()
) {
    val chatInputState by chatViewModel.chatInputState.collectAsState()

    Row(modifier) {
        TextField(
            value = chatInputState,
            onValueChange = { chatViewModel.onTextInputChange(it) },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                chatViewModel.onActionSend()
            }),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                textColor = MaterialTheme.colors.surface
            ),
            textStyle = TextStyle(
                color = MaterialTheme.colors.surface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
            ),
            maxLines = 6
        )
    }
}