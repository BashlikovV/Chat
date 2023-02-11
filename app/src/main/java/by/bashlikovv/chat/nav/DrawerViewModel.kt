package by.bashlikovv.chat.nav

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DrawerViewModel : ViewModel() {

    private val _drawerUiState = MutableStateFlow(DrawerUiState())
    val drawerState = _drawerUiState.asStateFlow()

    fun openDrawer() {
        _drawerUiState.update {
            it.copy(
                drawerState = DrawerState(DrawerValue.Open)
            )
        }
    }

    fun changeTheme() {
        _drawerUiState.update {
            it.copy(darkTheme = !it.darkTheme)
        }
    }
}