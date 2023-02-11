package by.bashlikovv.chat.navbar

import androidx.lifecycle.ViewModel
import by.bashlikovv.chat.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TopNavBarViewModel : ViewModel() {

    private val _topNavBarUiState = MutableStateFlow(TopNavBarState())
    val topNavBarState = _topNavBarUiState.asStateFlow()

    fun onBarChange(image: Int, description: String) {
        _topNavBarUiState.update {
            TopNavBarState(image, description)
        }
    }

    companion object {
        @JvmStatic val MENU_IMAGE = R.drawable.menu
        @JvmStatic val MENU_DESCRIPTION = "Menu"
        @JvmStatic val CLOSE_IMAGE = R.drawable.close
        @JvmStatic val CLOSE_DESCRIPTION = "Close"
    }
}