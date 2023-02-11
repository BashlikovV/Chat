package by.bashlikovv.chat.chat.message

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun MessagesList(
    data: MessagesListUiSate
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(1)
    ) {
        items(data.messages) { message ->
            Row {
                Text(text = message.message)
                Text(text = message.time)
            }
        }
    }
}