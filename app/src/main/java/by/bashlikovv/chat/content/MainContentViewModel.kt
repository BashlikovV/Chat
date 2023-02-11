package by.bashlikovv.chat.content

import androidx.lifecycle.ViewModel
import by.bashlikovv.chat.chat.message.MessagesListUiSate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainContentViewModel : ViewModel() {

    private val _mainContentUiState = MutableStateFlow(MainContentUiState())
    val mainContentUiState = _mainContentUiState.asStateFlow()

    fun setMainContent(data: MessagesListUiSate) {
        _mainContentUiState.update {
            MainContentUiState(data)
        }
    }
}