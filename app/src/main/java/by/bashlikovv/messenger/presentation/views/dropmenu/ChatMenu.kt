package by.bashlikovv.messenger.presentation.views.dropmenu

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import by.bashlikovv.messenger.R
import by.bashlikovv.messenger.presentation.viewmodel.ChatViewModel
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatMenu(chatViewModel: ChatViewModel = koinViewModel()) {
    val dMenuState by chatViewModel.dMenuExpanded.collectAsState()
    val screenWidth = LocalConfiguration.current.screenWidthDp

    DropdownMenu(
        expanded = dMenuState,
        onDismissRequest = { chatViewModel.onDMenuAction(false) },
        properties = PopupProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            focusable = true
        ),
        modifier = Modifier.background(MaterialTheme.colors.primary),
        offset = DpOffset(x = screenWidth.dp, y = 0.dp)
    ) {
        DropdownMenuItem(
            text = { ChatMenuTextItem("Clear history") },
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.history),
                    contentDescription = "Clear history",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary)
                )
            },
            onClick = {  }
        )
        Divider(color = MaterialTheme.colors.secondary)
        DropdownMenuItem(
            text = { ChatMenuTextItem("Delete chat") },
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.delete_outline),
                    contentDescription = "Delete chat",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary)
                )
            },
            onClick = { chatViewModel.onActionDeleteChat() },
            colors = androidx.compose.material3.MenuDefaults.itemColors(
                textColor = MaterialTheme.colors.secondary
            )
        )
    }
}

@Composable
fun ChatMenuTextItem(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = MaterialTheme.colors.secondary
    )
}