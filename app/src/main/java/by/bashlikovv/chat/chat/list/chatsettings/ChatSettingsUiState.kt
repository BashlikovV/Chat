package by.bashlikovv.chat.chat.list.chatsettings

import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

data class ChatSettingsUiState(
    val expanded: Boolean = false,
    val offset: DpOffset = DpOffset(0.dp, 0.dp)
)
