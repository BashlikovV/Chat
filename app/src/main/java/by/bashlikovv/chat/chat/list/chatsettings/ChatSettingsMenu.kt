package by.bashlikovv.chat.chat.list.chatsettings

import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.R

@Composable
fun ChatSettingsMenu(
    chatSettingsViewModel: ChatSettingsViewModel = viewModel()
) {
    val chatSettingsUiState by chatSettingsViewModel.chatSettingsUiState.collectAsState()

    DropdownMenu(
        expanded = chatSettingsUiState.expanded,
        offset = chatSettingsUiState.offset,
        onDismissRequest = { chatSettingsViewModel.onDismiss() },
    ) {
        DropdownMenuItem(
            onClick = {

                chatSettingsViewModel.onDismiss()
            }
        ) {
            Text(text = stringResource(R.string.read_chat))
        }
        DropdownMenuItem(
            onClick = {

                chatSettingsViewModel.onDismiss()
            }
        ) {
            Text(text = stringResource(R.string.unread_chat))
        }
    }
}