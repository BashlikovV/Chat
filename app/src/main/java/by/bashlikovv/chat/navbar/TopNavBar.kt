package by.bashlikovv.chat.navbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun TopNavBar(
    modifier: Modifier = Modifier,
    data: TopNavBarState
) {
    Row(
        modifier = modifier
            .fillMaxWidth(fraction = 1f)
    ) {
        Image(
            painterResource(data.leadingIcon),
            contentDescription = data.description,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
        )
    }
}