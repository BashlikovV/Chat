package by.bashlikovv.chat.app.screens.messenger

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import by.bashlikovv.chat.R

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
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        modifier = Modifier.fillMaxWidth().height(55.dp),
        containerColor = MaterialTheme.colors.primary
    ) {
        ContactsItem(
            selected = currentDestination?.hierarchy?.any { it.route == Screens.CONTACTS.name } == true,
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
        ChatsItem(
            selected = currentDestination?.hierarchy?.any { it.route == Screens.CHATS.name } == true,
            modifier = Modifier.padding(5.dp)
        ) {
            if (messengerUiState.newChat) {
                messengerViewModel.onAddChatClicked(false)
            }
            navHostController.navigate(it)
        }
        SettingsItem(
            selected = currentDestination?.hierarchy?.any { it.route == Screens.SETTINGS.name } == true,
            modifier = Modifier.padding(5.dp)
        ) {
            navHostController.navigate(it)
        }
    }
}
@Composable
private fun RowScope.ContactsItem(
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = { onClick(Screens.CONTACTS.name) },
        icon = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BottomBarImage(
                    image = R.drawable.user,
                    description = "Contacts"
                )
            }
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colors.primary,
            selectedTextColor = MaterialTheme.colors.primary,
            indicatorColor = MaterialTheme.colors.background
        ),
        modifier = modifier
    )
}

@Composable
private fun RowScope.ChatsItem(
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = { onClick(Screens.CHATS.name) },
        icon = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BottomBarImage(
                    image = R.drawable.message,
                    description = "Chats"
                )
            }
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colors.primary,
            selectedTextColor = MaterialTheme.colors.primary,
            indicatorColor = MaterialTheme.colors.background
        ),
        modifier = modifier
    )
}

@Composable
private fun RowScope.SettingsItem(
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = { onClick(Screens.SETTINGS.name) },
        icon = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BottomBarImage(
                    image = R.drawable.setting,
                    description = "Settings"
                )
            }
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colors.primary,
            selectedTextColor = MaterialTheme.colors.primary,
            indicatorColor = MaterialTheme.colors.background
        ),
        modifier = modifier
    )
}

@Composable
fun BottomBarImage(image: Int, description: String, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = image),
        contentDescription = description,
        contentScale = ContentScale.Crop,
        colorFilter = ColorFilter.tint(
            MaterialTheme.colors.primary,
            BlendMode.Dst
        ),
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(35.dp))
    )
}