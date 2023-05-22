package by.bashlikovv.chat.app.views.drawer

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.R
import by.bashlikovv.chat.app.LogInActivity
import by.bashlikovv.chat.app.screens.messenger.MessengerViewModel

private fun getDrawerContentConstraints(): ConstraintSet {
    return ConstraintSet {
        val userImage = createRefFor("userImage")
        val themeBtn = createRefFor("themeBtn")
        val username = createRefFor("username")

        constrain(userImage) {
            top.linkTo(anchor = parent.top, margin = 5.dp)
            start.linkTo(anchor = parent.start, margin = 5.dp)
        }
        constrain(themeBtn) {
            top.linkTo(anchor = parent.top, margin = 15.dp)
            end.linkTo(anchor = parent.end, margin = 15.dp)
        }
        constrain(username) {
            top.linkTo(anchor = userImage.bottom, margin = 5.dp)
            start.linkTo(anchor = parent.start, margin = 5.dp)
        }
    }
}

private fun constraints(): ConstraintSet {
    return ConstraintSet {
        val topElements = createRefFor("topElements")
        val bottomElements = createRefFor("bottomElements")

        constrain(topElements) {
            bottom.linkTo(anchor = bottomElements.top)
            start.linkTo(anchor = parent.start)
        }
        constrain(bottomElements) {
            top.linkTo(anchor = topElements.bottom)
            start.linkTo(anchor = parent.start)
        }
    }
}

private fun bottomItemsConstraint(): ConstraintSet {
    return ConstraintSet {
        val settingsBtn = createRefFor("settingsBtn")
        val contactsBtn = createRefFor("contactsBtn")
        val signOutBtn = createRefFor("signOutBtn")
        val spacer = createRefFor("spacer")
        val editUsername = createRefFor("editUsername")
        val bottomInput = createRefFor("bottomInput")

        constrain(settingsBtn) {
            top.linkTo(anchor = parent.top, margin = 5.dp)
            start.linkTo(anchor = parent.start)
        }
        constrain(contactsBtn) {
            top.linkTo(anchor = settingsBtn.bottom, margin = 5.dp)
            start.linkTo(anchor = parent.start)
        }
        constrain(spacer) {
            top.linkTo(anchor = contactsBtn.bottom, margin = 5.dp)
            start.linkTo(anchor = parent.start)
        }
        constrain(signOutBtn) {
            bottom.linkTo(anchor = parent.bottom, margin = 5.dp)
            start.linkTo(anchor = parent.start)
            top.linkTo(anchor = spacer.bottom, margin = 5.dp)
        }
        constrain(editUsername) {
            start.linkTo(anchor = parent.start)
            top.linkTo(anchor = spacer.bottom, margin = 5.dp)
        }
        constrain(bottomInput) {
            bottom.linkTo(anchor = parent.bottom, margin = 5.dp)
            start.linkTo(anchor = parent.start)
            top.linkTo(anchor = editUsername.bottom, margin = 5.dp)
        }
    }
}

@Composable
fun MessengerDrawerContent() {
    Column(Modifier.background(MaterialTheme.colors.primary)) {
        BoxWithConstraints {
            ConstraintLayout(constraintSet = constraints(), modifier = Modifier.fillMaxHeight()) {
                ConstraintLayout(
                    constraintSet = getDrawerContentConstraints(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.primary)
                        .layoutId("topElements")
                ) {
                    TopContent()
                }
                ConstraintLayout(
                    constraintSet = bottomItemsConstraint(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.primary)
                        .layoutId("bottomElements")
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                ) {
                    BottomContent()
                }
            }
        }
    }
}

@Composable
fun TopContent(messengerViewModel: MessengerViewModel = viewModel()) {
    val messengerUiState by messengerViewModel.messengerUiState.collectAsState()
    val me by messengerViewModel.me.collectAsState()
    var imageSize by remember { mutableStateOf(75.dp) }
    val size by animateDpAsState(targetValue = imageSize, label = "")

    Image(
        bitmap = me.userImage.userImageBitmap.asImageBitmap(),
        contentDescription = "user image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .clip(RoundedCornerShape(size = size - 60.dp))
            .background(MaterialTheme.colors.primary)
            .size(size = size)
            .layoutId("userImage")
            .clickable {
                if (imageSize == 75.dp) {
                    imageSize += 175.dp
                } else {
                    imageSize -= 175.dp
                }
            }
    )
    Image(
        painter = painterResource(
            if (messengerUiState.darkTheme)
                R.drawable.wb_cloudy
            else
                R.drawable.wb_sunny
        ),
        contentDescription = "theme",
        contentScale = ContentScale.Crop,
        colorFilter = ColorFilter.tint(color = MaterialTheme.colors.onError),
        modifier = Modifier
            .size(40.dp)
            .clickable { messengerViewModel.onThemeChange() }
            .layoutId("themeBtn")
    )
    Text(
        text = me.userName,
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.layoutId("username"),
        color = MaterialTheme.colors.surface
    )
}

@Composable
fun BottomContent(messengerViewModel: MessengerViewModel = viewModel()) {
    val context = LocalContext.current
    val activity = (LocalContext.current as? Activity)
    var settingsVisibility by rememberSaveable { mutableStateOf(false) }

    val topText = if (settingsVisibility) "Close settings" else "Settings"
    val topIcon by animateIntAsState(
        targetValue = if (settingsVisibility) R.drawable.arrow_back else R.drawable.settings,
        label = ""
    )
    var inputVisibility by rememberSaveable { mutableStateOf(false) }

    BottomContentItem(text = topText, layoutId = "settingsBtn", leadingIcon = topIcon) {
        settingsVisibility = !settingsVisibility
    }
    if (!settingsVisibility) {
        BottomContentItem(text = "Contacts", layoutId = "contactsBtn", leadingIcon = R.drawable.person) {}
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .layoutId("spacer")
        )
        BottomContentItem(text = "Sign out", layoutId = "signOutBtn", leadingIcon = R.drawable.exit) {
            messengerViewModel.onSignOut()
            val logInIntent = Intent(context, LogInActivity::class.java)
            context.startActivity(logInIntent)
            activity?.finish()
        }
    } else {
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .layoutId("spacer")
        )
        BottomContentItem(text = "Change username", layoutId = "editUsername", leadingIcon = R.drawable.edit) {
            inputVisibility = !inputVisibility
        }
        if(inputVisibility) {
            BottomInputField(layoutId = "bottomInput") {
                messengerViewModel.updateUsername(it)
                inputVisibility = false
            }
        }
    }
}

@Composable
fun BottomContentItem(
    text: String,
    layoutId: String,
    leadingIcon: Int,
    modifier: Modifier = Modifier,
    onClickListener: () -> Unit
) {
    Row(
        modifier = modifier
            .layoutId(layoutId)
            .clickable { onClickListener() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(leadingIcon),
            contentDescription = text,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(35.dp),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colors.surface)
        )
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colors.surface,
            modifier = Modifier
                .background(Color.Transparent)
                .fillMaxWidth()
                .padding(
                    start = 5.dp,
                    end = 5.dp,
                    top = 10.dp,
                    bottom = 10.dp
                )
        )
    }
}

@Composable
fun BottomInputField(
    layoutId: String,
    modifier: Modifier = Modifier,
    onClickListener: (String) -> Unit
) {
    var inputState by rememberSaveable { mutableStateOf("") }

    Row(
        modifier = modifier.layoutId(layoutId),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = inputState,
            onValueChange = { inputState = it},
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colors.surface,
                focusedIndicatorColor = MaterialTheme.colors.primary,
                unfocusedIndicatorColor = MaterialTheme.colors.primary
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onClickListener(inputState)
                    inputState = ""
                }
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
        )
        FloatingActionButton(
            onClick = {
                onClickListener(inputState)
                inputState = ""
            },
            backgroundColor = MaterialTheme.colors.primary,
            shape = AbsoluteCutCornerShape(0.dp),
            modifier = Modifier.padding(
                horizontal = 2.dp,
                vertical = 5.dp
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.build),
                contentDescription = "Confirm"
            )
        }
    }
}