package by.bashlikovv.chat.views.drawer

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.LogInActivity
import by.bashlikovv.chat.R
import by.bashlikovv.chat.screens.messenger.MessengerViewModel

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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessengerDrawerContent() {
    ModalDrawerSheet(drawerContainerColor = MaterialTheme.colors.primary) {
        BoxWithConstraints {
            ConstraintLayout(constraintSet = constraints(), modifier = Modifier.fillMaxHeight()) {
                ConstraintLayout(
                    constraintSet = getDrawerContentConstraints(),
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colors.primary).layoutId("topElements")
                ) {
                    TopContent()
                }
                ConstraintLayout(
                    constraintSet = bottomItemsConstraint(),
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colors.primary).layoutId("bottomElements"),
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

    Image(
        painter = painterResource(R.drawable.test_face_man),
        contentDescription = "user image",
        contentScale = ContentScale.Crop,
        modifier = Modifier.clip(RoundedCornerShape(15.dp)).background(MaterialTheme.colors.secondary).size(75.dp)
            .layoutId("userImage")
    )
    Image(
        painter = painterResource(if (messengerUiState.darkTheme) R.drawable.wb_cloudy else R.drawable.wb_sunny),
        contentDescription = "theme",
        contentScale = ContentScale.Crop,
        colorFilter = ColorFilter.tint(color = MaterialTheme.colors.secondary),
        modifier = Modifier.size(40.dp).clickable { messengerViewModel.onThemeChange() }.layoutId("themeBtn")
    )
    Text(
        text = messengerUiState.me.userName, fontSize = 24.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.layoutId("username"),
        color = MaterialTheme.colors.secondary
    )
}

@Composable
fun BottomContent(messengerViewModel: MessengerViewModel = viewModel()) {
    val context = LocalContext.current
    val activity = (LocalContext.current as? Activity)

    BottomContentItem(text = "Settings", layoutId = "settingsBtn", leadingIcon = R.drawable.settings) {}
    BottomContentItem(text = "Contacts", layoutId = "contactsBtn", leadingIcon = R.drawable.person) {}
    Spacer(
        modifier = Modifier.height(1.dp).fillMaxWidth().background(MaterialTheme.colors.secondary).layoutId("spacer")
    )
    BottomContentItem(text = "Sign out", layoutId = "signOutBtn", leadingIcon = R.drawable.exit) {
        messengerViewModel.onSignOut()
        val logInIntent = Intent(context, LogInActivity::class.java)
        context.startActivity(logInIntent)
        activity?.finish()
    }
}

@Composable
fun BottomContentItem(text: String, layoutId: String, leadingIcon: Int, onClickListener: () -> Unit) {
    Row(
        modifier = Modifier.layoutId(layoutId).clickable { onClickListener() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(leadingIcon),
            contentDescription = text,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(35.dp),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colors.secondary)
        )
        Text(
            text = text, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colors.secondary,
            modifier = Modifier
                .background(Color.Transparent).fillMaxWidth().padding(
                    start = 5.dp,
                    end = 5.dp,
                    top = 10.dp,
                    bottom = 10.dp
                )
        )
    }
}