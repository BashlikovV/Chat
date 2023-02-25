package by.bashlikovv.chat.chat

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@DelicateCoroutinesApi
@Composable
fun BottomInputFiled(chatViewModel: ChatViewModel = viewModel()) {
    val chatUiState by chatViewModel.chatUiState.collectAsState()
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colors.primary).animateContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.emoji),
            contentDescription = "Emoji selection",
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colors.secondary),
            modifier = Modifier.padding(horizontal = 5.dp).size(35.dp).fillMaxWidth(0.1f)
                .clickable {  }
        )
        TextField(
            value = chatUiState.textInputState,
            onValueChange = { chatViewModel.onTextInputChange(it) },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                GlobalScope.launch {
                    chatViewModel.onActionSend()
                }
            }),
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.primary,
                textColor = MaterialTheme.colors.secondary
            )
        )
        Image(
            painter = painterResource(R.drawable.folder_open),
            contentDescription = "Open file",
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colors.secondary),
            modifier = Modifier.padding(horizontal = 5.dp).size(35.dp).fillMaxWidth(0.1f)
                .clickable {  }
        )
        Image(
            painter = painterResource(if (chatUiState.isCanSend) R.drawable.send else  R.drawable.camera),
            contentDescription = "Open camera",
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colors.secondary),
            modifier = Modifier.padding(horizontal = 5.dp).size(35.dp).fillMaxWidth(0.1f)
                .clickable {
                    if (chatUiState.isCanSend) {
                        GlobalScope.launch {
                            chatViewModel.onActionSend()
                        }
                    } else {
                        chatViewModel.onActionGallery(context)
                    }
                }
        )
    }
}