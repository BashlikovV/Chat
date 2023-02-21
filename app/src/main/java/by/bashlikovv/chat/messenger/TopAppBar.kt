package by.bashlikovv.chat.messenger

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.NavigationBar
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.R

@Composable
fun TopAppBar() {
    NavigationBar(
        modifier = Modifier.fillMaxWidth().height(55.dp).padding(bottom = 1.dp)
    ) {
        TopNavBarContent()
    }
}

private fun getNavBarContentConstraints(): ConstraintSet {
    return ConstraintSet {
        val leftItem = createRefFor("leftItem")
        val rightItems = createRefFor("rightItems")

        constrain(leftItem) {
            top.linkTo(anchor = parent.top)
            bottom.linkTo(anchor = parent.bottom)
            start.linkTo(anchor = parent.start)
        }
        constrain(rightItems) {
            top.linkTo(anchor = parent.top)
            bottom.linkTo(anchor = parent.bottom)
            end.linkTo(anchor = parent.end)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TopNavBarContent(messengerViewModel: MessengerViewModel = viewModel()) {
    val messengerUiState by messengerViewModel.messengerUiState.collectAsState()


    BoxWithConstraints(modifier = Modifier.height(55.dp)) {
        val constraintSet = getNavBarContentConstraints()

        ConstraintLayout(
            constraintSet = constraintSet,
            modifier = Modifier.fillMaxWidth().height(55.dp).background(MaterialTheme.colors.primary)
        ) {
            Image(
                painter = painterResource(R.drawable.menu),
                contentDescription = "Menu",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        messengerViewModel.onActionMenu()
                    }
                    .layoutId("leftItem")
            )
            AnimatedVisibility(visible = messengerUiState.visible, modifier = Modifier.layoutId("rightItems")) {
                Row {
                    Image(
                        painter = painterResource(R.drawable.pin),
                        contentDescription = "Pin chat with ${messengerUiState.selectedItem.user.userName}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(40.dp).clickable {
                            messengerViewModel.onActionPin()
                        }
                    )
                    Image(
                        painter = painterResource(R.drawable.mark_chat_read),
                        contentDescription = "mark chat as read",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                messengerViewModel.onActionRead(messengerUiState.selectedItem)
                            }
                    )
                    Image(
                        painter = painterResource(R.drawable.delete_outline),
                        contentDescription = "delete",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                messengerViewModel.onActionDelete(messengerUiState.selectedItem)
                            }
                    )
                }
            }
        }
    }
}