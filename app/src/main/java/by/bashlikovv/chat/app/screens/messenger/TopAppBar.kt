package by.bashlikovv.chat.app.screens.messenger

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.R
import by.bashlikovv.chat.app.struct.Chat

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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TopAppBar(messengerViewModel: MessengerViewModel = viewModel()) {
    val messengerUiState by messengerViewModel.messengerUiState.collectAsState()

    NavigationBar(
        modifier = Modifier.fillMaxWidth().padding(bottom = 1.dp).animateContentSize().height(55.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.background(MaterialTheme.colors.primary).height(55.dp)
        ) {
            val constraintSet = getNavBarContentConstraints()

            ConstraintLayout(
                constraintSet = constraintSet,
                modifier = Modifier.fillMaxWidth()
                    .height(if (messengerUiState.expanded) LocalConfiguration.current.screenHeightDp.dp else 55.dp)
                    .background(MaterialTheme.colors.primary),
                optimizationLevel = 10
            ) {
                LeftItem()
                RightItems()
            }
        }
    }
}

@Composable
fun LeftItem(messengerViewModel: MessengerViewModel = viewModel()) {
    val messengerUiState by messengerViewModel.messengerUiState.collectAsState()

    AnimatedVisibility(visible = true, modifier = Modifier.layoutId("leftItem")) {
        Image(
            painter = painterResource(if (messengerUiState.expanded) R.drawable.arrow_back else R.drawable.menu),
            contentDescription = if (messengerUiState.expanded) "Close search" else "Menu",
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colors.secondary),
            modifier = Modifier
                .size(40.dp)
                .clickable {
                    if (messengerUiState.expanded) {
                        messengerViewModel.onSearchClick(false)
                    } else {
                        messengerViewModel.onActionMenu()
                    }
                }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RightItems(messengerViewModel: MessengerViewModel = viewModel()) {
    val messengerUiState by messengerViewModel.messengerUiState.collectAsState()

    AnimatedVisibility(visible = true, modifier = Modifier.layoutId("rightItems")) {
        AnimatedContent(
            targetState = messengerUiState.expanded,
            transitionSpec = {
                fadeIn(animationSpec = tween(150, 150)) with
                    fadeOut(animationSpec = tween(150)) using
                        SizeTransform { initialSize, targetSize ->
                            if (targetState) {
                                keyframes {
                                    IntSize(targetSize.width, initialSize.height) at 150
                                    durationMillis = 300
                                }
                            } else {
                                keyframes {
                                    IntSize(initialSize.width, targetSize.height) at 150
                                    durationMillis = 300
                                }
                            }
                        }
            }
        ) { targetState ->
            if (targetState) {
                Expanded()
            } else {
                ContentIcon()
            }
        }
        AnimatedVisibility(visible = messengerUiState.visible) {
            Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                RightItem(
                    image = R.drawable.pin,
                    contentDescription = "Pin chat with ${messengerUiState.selectedItem.user.userName}",
                    chat = messengerUiState.selectedItem
                ) { messengerViewModel.onActionPin(it) }
                RightItem(
                    image = R.drawable.mark_chat_read,
                    contentDescription = "mark chat with ${messengerUiState.selectedItem.user.userName} as read",
                    chat = messengerUiState.selectedItem
                ) { messengerViewModel.onActionRead(it) }
                RightItem(
                    image = R.drawable.delete_outline,
                    contentDescription = "delete chat with \${messengerUiState.selectedItem.user.userName} outline",
                    chat = messengerUiState.selectedItem
                ) { messengerViewModel.onActionDelete(it) }
            }
        }
    }
}

@Composable
fun RightItem(image: Int, contentDescription: String, chat: Chat, actionListener: (Chat) -> Unit) {
    Image(
        painter = painterResource(image),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        colorFilter = ColorFilter.tint(color = MaterialTheme.colors.secondary),
        modifier = Modifier.size(40.dp).clickable {
            actionListener(chat)
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Expanded(messengerViewModel: MessengerViewModel = viewModel()) {
    val messengerUiState by messengerViewModel.messengerUiState.collectAsState()

    OutlinedTextField(
        value = messengerUiState.searchInput,
        onValueChange = {
            messengerViewModel.onSearchInputChange(it)
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                messengerViewModel.onSearchClick(false)
            }
        ),
        maxLines = 1,
        modifier = Modifier
            .background(MaterialTheme.colors.primary)
            .padding(
                top = 3.dp,
                bottom = 2.5.dp,
                end = 5.dp,
                start = 5.dp
            )
            .fillMaxWidth(0.9f),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            textColor = MaterialTheme.colors.secondary,
            focusedIndicatorColor = MaterialTheme.colors.secondary,
            unfocusedIndicatorColor = MaterialTheme.colors.secondary,
            cursorColor = MaterialTheme.colors.secondary
        )
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContentIcon(messengerViewModel: MessengerViewModel = viewModel()) {
    val messengerUiState by messengerViewModel.messengerUiState.collectAsState()

    AnimatedVisibility(visible = !messengerUiState.visible) {
        Image(
            painter = painterResource(R.drawable.search),
            contentDescription = "Search",
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colors.secondary),
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(25.dp))
                .clickable {
                    messengerViewModel.onSearchClick(false)
                    messengerViewModel.onSearchInputChange("")
                }
        )
    }
}