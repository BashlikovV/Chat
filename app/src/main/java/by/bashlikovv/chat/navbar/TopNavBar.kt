package by.bashlikovv.chat.navbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import by.bashlikovv.chat.chat.list.ChatListViewModel

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
        TopNavBarContent(topNavBarState) {
            onClickListener(topNavBarState.description)
        }
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

@Composable
fun TopNavBarContent(
    topNavBarState: TopNavBarState,
    topNavBarViewModel: TopNavBarViewModel = viewModel(),
    chatListViewModel: ChatListViewModel = viewModel(),
    onClickListener: (String) -> Unit
) {
    BoxWithConstraints {
        val constraintSet = getNavBarContentConstraints()

        ConstraintLayout(
            constraintSet = constraintSet,
            modifier = Modifier.fillMaxWidth()
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
                    .layoutId("leftItem")
            )
            AnimatedVisibility(
                visible = TopNavBarViewModel.CLOSE_IMAGE == topNavBarState.leadingIcon,
                modifier = Modifier.layoutId("rightItems")
            ) {
                Row {
                    Image(
                        painter = painterResource(R.drawable.pin),
                        contentDescription = "pin",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                onClickListener(topNavBarState.description)
                            }
                    )
                    Image(
                        painter = painterResource(R.drawable.mark_chat_read),
                        contentDescription = "mark chat as read",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                onClickListener(topNavBarState.description)
                                topNavBarViewModel.onReadElem(chatListViewModel.selectedItem.value, chatListViewModel)
                            }
                    )
                    Image(
                        painter = painterResource(R.drawable.delete_outline),
                        contentDescription = "delete",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                onClickListener(topNavBarState.description)
                                topNavBarViewModel.onRemoveElem(chatListViewModel.selectedItem.value, chatListViewModel)
                            }
                    )
                }
            }
        }
    }
}