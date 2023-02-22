package by.bashlikovv.chat.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.R
import by.bashlikovv.chat.messenger.MessengerViewModel

private fun getDrawerContentConstraints(): ConstraintSet {
    return ConstraintSet {
        val userImage = createRefFor("userImage")
        val themeBtn = createRefFor("themeBtn")
        val username = createRefFor("username")

        constrain(userImage) {
            top.linkTo(anchor = parent.top, margin = 5.dp)
            start.linkTo(anchor = parent.start, margin = 5.dp)
            end.linkTo(anchor = parent.end, margin = 5.dp)
        }
        constrain(themeBtn) {
            top.linkTo(anchor = parent.top, margin = 5.dp)
            end.linkTo(anchor = parent.end, margin = 5.dp)
        }
        constrain(username) {
            top.linkTo(anchor = userImage.bottom, margin = 5.dp)
            start.linkTo(anchor = parent.start)
            end.linkTo(anchor = parent.end)
        }
    }
}

private fun constraints(): ConstraintSet {
    return ConstraintSet {
        val topElements = createRefFor("topElements")
        val bottomElements = createRefFor("bottomElements")

        constrain(topElements) {
            bottom.linkTo(bottomElements.top)
        }
        constrain(bottomElements) {
            top.linkTo(topElements.bottom)
        }
    }
}

private fun bottomItemsConstraint(): ConstraintSet {
    return ConstraintSet {
        val settingsBtn = createRefFor("settingsBtn")
        val contactsBtn = createRefFor("contactsBtn")

        constrain(settingsBtn) {
            top.linkTo(anchor = parent.top, margin = 5.dp)
            start.linkTo(anchor = parent.start)
        }
        constrain(contactsBtn) {
            top.linkTo(anchor = settingsBtn.bottom, margin = 5.dp)
            start.linkTo(anchor = parent.start)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent() {
    ModalDrawerSheet(drawerContainerColor = MaterialTheme.colors.primary) {
        BoxWithConstraints(modifier = Modifier) {
            ConstraintLayout(constraintSet = constraints()) {
                ConstraintLayout(
                    constraintSet = getDrawerContentConstraints(),
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colors.primary).layoutId("topElements")
                ) {
                    TopContent()
                }
                ConstraintLayout(
                    constraintSet = bottomItemsConstraint(),
                    modifier = Modifier.background(MaterialTheme.colors.primary).layoutId("bottomElements"),
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
        modifier = Modifier.clip(RoundedCornerShape(15.dp)).background(MaterialTheme.colors.secondary).size(200.dp)
            .layoutId("userImage")
    )
    FloatingActionButton(
        backgroundColor = MaterialTheme.colors.primary,
        onClick = {
            messengerViewModel.onThemeChange()
        },
        shape = RoundedCornerShape(50.dp), modifier = Modifier.layoutId("themeBtn")
    ) {
        Image(
            painter = painterResource(if (messengerUiState.darkTheme) R.drawable.wb_cloudy else R.drawable.wb_sunny),
            contentDescription = "theme",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(25.dp)
        )
    }
    Text(
        text = "User name", fontSize = 24.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.layoutId("username")
    )
}

@Composable
fun BottomContent() {
    BottomContentItem(text = "Settings", layoutId = "settingsBtn") {}
    BottomContentItem(text = "Contacts", layoutId = "contactsBtn") {}
}

@Composable
fun BottomContentItem(text: String, layoutId: String, onClickListener: () -> Unit) {
    TextButton(
        onClick = { onClickListener() },
        modifier = Modifier.layoutId(layoutId).background(MaterialTheme.colors.primary).fillMaxWidth()
    ) {
        Text(
            text = text, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colors.secondary
        )
    }
}