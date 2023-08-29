package by.bashlikovv.messenger.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.bashlikovv.messenger.data.remote.model.ServerUser
import by.bashlikovv.messenger.domain.model.Chat
import by.bashlikovv.messenger.domain.model.Message
import by.bashlikovv.messenger.domain.model.User
import by.bashlikovv.messenger.domain.usecase.GetAccountOfflineUseCase
import by.bashlikovv.messenger.domain.usecase.GetUserImageOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.GetUserOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.GetUsersOnlineUseCase
import by.bashlikovv.messenger.presentation.view.messenger.users.UsersUiState
import by.bashlikovv.messenger.utils.SecurityUtilsImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

class UsersViewModel(
    private val getUserOnlineUseCase: GetUserOnlineUseCase,
    private val getAccountOfflineUseCase: GetAccountOfflineUseCase,
    private val getUsersOnlineUseCase: GetUsersOnlineUseCase,
    private val getUserImageOnlineUseCase: GetUserImageOnlineUseCase
) : ViewModel() {

    private val _usersUiState: MutableStateFlow<UsersUiState> = MutableStateFlow(UsersUiState())
    var usersUiState: StateFlow<UsersUiState> = _usersUiState.asStateFlow()

    private val _me = MutableStateFlow(User())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val result = suspendCancellableCoroutine<User> { continuation ->
                continuation.invokeOnCancellation { cancel() }

                viewModelScope.launch(Dispatchers.IO) {
                    getAccountOfflineUseCase.execute().collectLatest {
                        getUserOnlineUseCase.execute(
                            it?.token ?: ""
                        )
                    }
                }
            }
            _me.update { result }
        }
    }

    fun getUsers(input: String) = viewModelScope.launch(Dispatchers.IO) {
//        messengerViewModel.setUpdateVisibility(true)
        val result = suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation { cancel() }

            viewModelScope.launch(Dispatchers.IO) {
                _usersUiState.update { UsersUiState(listOf()) }
                getSearchOutput(input)
                continuation.resumeWith(Result.success(false))
            }
        }
//        messengerViewModel.setUpdateVisibility(result)
    }

    private suspend fun getSearchOutput(input: String) {
        var serverUsers: List<ServerUser> = emptyList()
        try {
            viewModelScope.launch {
                getAccountOfflineUseCase.execute().collectLatest {
                    serverUsers = getUsersOnlineUseCase.execute(it?.token ?: "")
                }
            }
        } catch (e: Exception) {
            processSearchException(e)
            return
        }
        if (input.isEmpty()) {
            processEmptyInput(serverUsers)
        } else {
            processCorrectInput(serverUsers, input)
        }
    }

    private suspend fun processCorrectInput(
        serverUsers: List<ServerUser>,
        input: String
    ) {
        serverUsers.forEach { serverUser ->
            if (input.length <= serverUser.username.length) {
                val subStr = serverUser.username.subSequence(0, input.length).toString().lowercase()
                if (subStr == input.lowercase() && subStr != "") {
                    val tmp = Chat(
                        user = User(
                            userName = serverUser.username,
                            userToken = SecurityUtilsImpl().bytesToString(serverUser.token),
                            userImage = getUserImageOnlineUseCase.execute(serverUser.image.decodeToString())
                        ),
                        messages = listOf(Message(value = "")),
                        time = ""
                    )
                    _usersUiState.update { state ->
                        state.copy(chats = state.chats + tmp)
                    }
                }
            }
        }
    }

    private suspend fun processEmptyInput(
        serverUsers: List<ServerUser>
    ) {
        serverUsers.forEach { serverUser ->
            val tmp = Chat(
                user = User(
                    userName = serverUser.username,
                    userToken = SecurityUtilsImpl().bytesToString(serverUser.token),
                    userImage = getUserImageOnlineUseCase.execute(serverUser.image.decodeToString())
                ),
                messages = listOf(Message(value = "")),
                time = ""
            )
            _usersUiState.update { state ->
                state.copy(chats = state.chats + tmp)
            }
        }
    }

    private fun processSearchException(e: Exception) {
        _usersUiState.update {
            it.copy(
                chats = listOf(
                    Chat(
                        user = User(userName = e.message ?: "Network error"),
                        messages = listOf(Message(value = ""))
                    )
                )
            )
        }
    }
}