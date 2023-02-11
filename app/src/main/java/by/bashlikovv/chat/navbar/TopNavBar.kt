package by.bashlikovv.chat.navbar

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.R

@Composable
fun TopNavBar(
    modifier: Modifier = Modifier,
    topNavBarViewModel: TopNavBarViewModel = viewModel(),
    onClickListener: (String) -> Unit
) {
    val topNavBarState by topNavBarViewModel.topNavBarState.collectAsState()

    Row(
        modifier = modifier
            .fillMaxWidth(fraction = 1f)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Image(
            painterResource(topNavBarState.leadingIcon),
            contentDescription = topNavBarState.description,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clickable {
                    onClickListener(topNavBarState.description)
                }
        )
        if (TopNavBarViewModel.CLOSE_IMAGE == topNavBarState.leadingIcon) {
            Image(
                painter = painterResource(R.drawable.pin),
                contentDescription = "pin",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        onClickListener(topNavBarState.description)
                    }
            )
            Image(
                painter = painterResource(R.drawable.delete_outline),
                contentDescription = "delete",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        onClickListener(topNavBarState.description)
                    }
            )
        }
    }
}