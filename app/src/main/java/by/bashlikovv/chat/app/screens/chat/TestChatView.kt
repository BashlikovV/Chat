package by.bashlikovv.chat.app.screens.chat

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import by.bashlikovv.chat.R

@Preview
@Composable
fun TestChatView() {
    Scaffold(
        topBar = { TopChatBar() },
        bottomBar = { BottomChatBar() }
    ) {
        ChatView(modifier = Modifier
            .padding(it)
            .fillMaxSize())
    }
}

@Composable
fun TopChatBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colors.primary)
    ) {
        Column(
            modifier = Modifier.weight(0.7f),
            verticalArrangement = Arrangement.Center
        ) {
            TopBarLeftContent()
        }
        Column(modifier = Modifier.weight(0.3f)) {
            TopBarRightContent()
        }
    }
}

@Composable
private fun TopBarLeftContent() {
    Row(
        modifier = Modifier
            .padding(start = 20.dp)
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            painter = painterResource(id = R.drawable.arrow_back),
            contentDescription = "back",
            tint = MaterialTheme.colors.onError,
            modifier = Modifier
                .size(24.dp)
                .clickable { }
        )
        ChatImageView()
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Brix Templates",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.colors.secondary
            )
            Text(
                text = "Activ 19m ago",
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = MaterialTheme.colors.secondary
            )
        }
    }
}

@Composable
private fun ChatImageView(
    isActive: Boolean = false
) {
    Box(
        modifier = Modifier.padding(start = 27.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Image(
            painter = painterResource(id = R.drawable.user),
            contentDescription = "Icon",
            modifier = Modifier.size(40.dp)
        )
        if (isActive) {
            ActivityIcon(icon = R.drawable.avatarbadge, isActive = true)
        } else {
            ActivityIcon(icon = R.drawable.avatarunfilledbadge, isActive = false)
        }
    }
}

@Composable
private fun ActivityIcon(@DrawableRes icon: Int, isActive: Boolean) {
    Icon(
        painter = painterResource(id = icon),
        contentDescription = if (isActive) "Online" else "Offline",
        tint = MaterialTheme.colors.onError,
        modifier = Modifier.size(10.dp)
    )
}

@Composable
private fun TopBarRightContent() {
    Row(
        modifier = Modifier
            .padding(end = 20.dp)
            .fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(30.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TopBarRightIcon(R.drawable.call, "call") {  }
        TopBarRightIcon(R.drawable.video, "video call") {  }
    }
}

@Composable
private fun TopBarRightIcon(
    @DrawableRes icon: Int,
    description: String,
    onClick: () -> Unit
) {
    Icon(
        painter = painterResource(id = icon),
        contentDescription = description,
        tint = MaterialTheme.colors.onError,
        modifier = Modifier
            .size(24.dp)
            .clickable { onClick() }
    )
}

@Composable
fun BottomChatBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colors.primary),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Row(modifier = Modifier.padding(start = 5.dp).weight(0.1f)) {
            BottomBarIcon(R.drawable.emoji, "choose emoji") {  }
        }
        BottomBarInputField(modifier = Modifier.weight(0.7f))
        Row(
            modifier = Modifier.padding(end = 5.dp).weight(0.2f),
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.End)
        ) {
            BottomBarIcon(R.drawable.folder_open, "add file") {  }
            BottomBarIcon(R.drawable.micro, "send voice message") {  }
        }
    }
}

@Composable
private fun BottomBarIcon(
    @DrawableRes icon: Int,
    description: String,
    onClick: () -> Unit
) {
    Icon(
        painter = painterResource(id = icon),
        contentDescription = description,
        tint = MaterialTheme.colors.onError,
        modifier = Modifier
            .size(26.dp)
            .clickable { onClick() }
    )
}

@Composable
private fun BottomBarInputField(modifier: Modifier = Modifier) {
    var inputState by rememberSaveable {
        mutableStateOf("")
    }

    Row(modifier) {
        TextField(
            value = inputState,
            onValueChange = { inputState = it },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                textColor = MaterialTheme.colors.secondary
            ),
            textStyle = TextStyle(
                color = MaterialTheme.colors.secondary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
            )
        )
    }
}

@Composable
fun ChatView(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {

    }
}