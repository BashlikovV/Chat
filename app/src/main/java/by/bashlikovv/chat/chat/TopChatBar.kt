package by.bashlikovv.chat.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.R

private fun getTopChatBarConstraints(): ConstraintSet {
    return ConstraintSet {
        val closeChat = createRefFor("closeChat")
        val more = createRefFor("more")

        constrain(closeChat) {
            start.linkTo(anchor = parent.start, margin = 5.dp)
            top.linkTo(anchor = parent.top)
            bottom.linkTo(anchor = parent.bottom)
        }
        constrain(more) {
            end.linkTo(anchor = parent.end, margin = 5.dp)
            top.linkTo(anchor = parent.top)
            bottom.linkTo(anchor = parent.bottom)
        }
    }
}

@Composable
fun TopChatBar(chatViewModel: ChatViewModel = viewModel(), onBackAction: () -> Unit) {

    BoxWithConstraints {
        ConstraintLayout(
            constraintSet = getTopChatBarConstraints(),
            modifier = Modifier.fillMaxWidth().height(55.dp).background(MaterialTheme.colors.primary)
        ) {
            Image(
                painter = painterResource(R.drawable.arrow_back),
                contentDescription = "Close chat",
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colors.secondary),
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        onBackAction()
                    }.layoutId("closeChat")
            )
            Image(
                painter = painterResource(R.drawable.more_vert),
                contentDescription = "More",
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colors.secondary),
                modifier = Modifier
                    .size(40.dp)
                    .clickable {

                    }.layoutId("more")
            )
        }
    }
}