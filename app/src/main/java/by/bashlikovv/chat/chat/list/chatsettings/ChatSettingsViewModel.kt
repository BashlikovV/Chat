package by.bashlikovv.chat.chat.list.chatsettings

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChatSettingsViewModel : ViewModel() {

    private val _chatSettingsUiState = MutableStateFlow(ChatSettingsUiState())
    val chatSettingsUiState = _chatSettingsUiState.asStateFlow()

    fun onDismiss() {
        _chatSettingsUiState.update { chatSettingsUiState ->
            chatSettingsUiState.copy(expanded = false)
        }
    }

    fun onExpand(context: Context, offset: Offset) {
        _chatSettingsUiState.update { chatSettingsUiState ->
            chatSettingsUiState.copy(
                expanded = true,
                offset = getDpOffset(context, offset)
            )
        }
    }

    private fun getDpOffset(context: Context, offset: Offset): DpOffset {
        val screenPixelDensity = context.resources.displayMetrics.density
        val xDpValue = (offset.x / screenPixelDensity).dp
        val yDpValue = (offset.y / screenPixelDensity).dp - 250.dp
        return DpOffset(xDpValue, yDpValue)
    }
}