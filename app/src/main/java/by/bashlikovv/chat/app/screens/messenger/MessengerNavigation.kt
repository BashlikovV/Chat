package by.bashlikovv.chat.app.screens.messenger

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import by.bashlikovv.chat.R
import by.bashlikovv.chat.app.views.bottombar.AnimatedNavigationBar
import by.bashlikovv.chat.app.views.bottombar.animation.Height

enum class Screens {
    CONTACTS, CHATS, SETTINGS
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MessengerBottomNavigationBar(
    navHostController: NavHostController,
    messengerViewModel: MessengerViewModel = viewModel()
) {
    val messengerUiState by messengerViewModel.messengerUiState.collectAsState()
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()

    AnimatedNavigationBar(
        modifier = Modifier.fillMaxWidth().height(55.dp),
        selectedIndex = when(navBackStackEntry?.destination?.route) {
            Screens.CONTACTS.name -> 0
            Screens.CHATS.name -> 1
            else -> 2
        },
        barColor = MaterialTheme.colors.primary,
        indentAnimation = Height(tween(500), indentWidth = 50.dp, indentHeight = 7.dp)
    ) {
        Row {
            ContactsItem(
                selected = navBackStackEntry?.destination?.route == Screens.CONTACTS.name,
                modifier = Modifier.padding(5.dp)
            ) {
                if (messengerUiState.newChat) {
                    messengerViewModel.onAddChatClicked(false)
                    navHostController.navigate(Screens.CHATS.name)
                } else {
                    messengerViewModel.onAddChatClicked(true)
                    messengerViewModel.onSearchInputChange("")
                    navHostController.navigate(it)
                }
            }
        }
        Row {
            ChatsItem(
                selected = navBackStackEntry?.destination?.route == Screens.CHATS.name,
                modifier = Modifier.padding(5.dp)
            ) {
                if (messengerUiState.newChat) {
                    messengerViewModel.onAddChatClicked(false)
                }
                navHostController.navigate(it)
            }
        }
        Row {
            SettingsItem(
                selected = navBackStackEntry?.destination?.route == Screens.SETTINGS.name,
                modifier = Modifier.padding(5.dp)
            ) {
                navHostController.navigate(it)
            }
        }
    }
}
@Composable
private fun RowScope.ContactsItem(
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    var targetValue by remember { mutableStateOf(1f) }
    val animation by animateFloatAsState(targetValue = targetValue, label = "")

    NavigationBarItem(
        selected = selected,
        onClick = { onClick(Screens.CONTACTS.name) },
        icon = { NavigationBarIcon(R.drawable.user, "Contacts") },
        colors = navigationBarItemColors(),
        modifier = modifier.graphicsLayer {
            targetValue = if (selected) {
                0.8f
            } else {
                1f
            }
            scaleX = animation
            scaleY = animation
        }
    )
}

@Composable
private fun RowScope.ChatsItem(
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    var targetValue by remember { mutableStateOf(1f) }
    val animation by animateFloatAsState(targetValue = targetValue, label = "")

    NavigationBarItem(
        selected = selected,
        onClick = { onClick(Screens.CHATS.name) },
        icon = { NavigationBarIcon(R.drawable.message, "Chats") },
        colors = navigationBarItemColors(),
        modifier = modifier.graphicsLayer {
            targetValue = if (selected) {
                0.8f
            } else {
                1f
            }
            scaleX = animation
            scaleY = animation
        }
    )
}

@Composable
private fun RowScope.SettingsItem(
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    var targetValue by remember { mutableStateOf(1f) }
    val animation by animateFloatAsState(targetValue = targetValue, label = "")

    NavigationBarItem(
        selected = selected,
        onClick = { onClick(Screens.SETTINGS.name) },
        icon = { NavigationBarIcon(R.drawable.setting, description = "Settings") },
        colors = navigationBarItemColors(),
        modifier = modifier.graphicsLayer {
            targetValue = if (selected) {
                0.8f
            } else {
                1f
            }
            scaleX = animation
            scaleY = animation
        }
    )
}

@Composable
private fun NavigationBarIcon(@DrawableRes icon: Int, description: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = description,
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(
                MaterialTheme.colors.primary,
                BlendMode.Dst
            ),
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(35.dp))
        )
    }
}

@Composable
private fun navigationBarItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colors.primary,
    selectedTextColor = MaterialTheme.colors.primary,
    indicatorColor = MaterialTheme.colors.background
)