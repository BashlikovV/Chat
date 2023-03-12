package by.bashlikovv.chat.views.fab

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.R
import by.bashlikovv.chat.screens.messenger.MessengerViewModel

@Composable
fun MessengerFABContent(messengerViewModel: MessengerViewModel = viewModel()) {
    val context = LocalContext.current

    FloatingActionButton(
        onClick = {
            messengerViewModel.onAddChatClicked(context)
        },
        shape = RoundedCornerShape(25.dp),
        backgroundColor = MaterialTheme.colors.primary,
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