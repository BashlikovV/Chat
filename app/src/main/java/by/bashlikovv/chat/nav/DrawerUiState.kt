package by.bashlikovv.chat.nav

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import by.bashlikovv.chat.R

data class DrawerUiState(
    val userImage: Int = R.drawable.test_face_man,
    val userName: String = "User name",
    val darkTheme: Boolean = true,
    val drawerState: DrawerState = DrawerState(DrawerValue.Closed)
)
