package by.bashlikovv.chat.views.fab

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.R
import by.bashlikovv.chat.screens.messenger.MessengerViewModel

@Composable
fun MessengerFABContent(messengerViewModel: MessengerViewModel = viewModel()) {
    val messengerUiState by messengerViewModel.messengerUiState.collectAsState()

    FilledIconButton(
        onClick = {
            messengerViewModel.onAddChatClicked(!messengerUiState.newChat)
        },
        shape = RoundedCornerShape(25.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.secondary
        ),
        modifier = Modifier.size(50.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.add),
            contentDescription = "Add new chat",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(40.dp),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colors.secondary)
        )
    }
}