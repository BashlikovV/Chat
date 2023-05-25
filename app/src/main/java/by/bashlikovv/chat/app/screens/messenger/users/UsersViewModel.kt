package by.bashlikovv.chat.app.screens.messenger.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.bashlikovv.chat.app.model.accounts.AccountsRepository
import by.bashlikovv.chat.app.model.users.OkHTTPUsersRepository
import by.bashlikovv.chat.app.model.users.UsersRepository
import by.bashlikovv.chat.app.screens.messenger.MessengerViewModel
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.struct.Message
import by.bashlikovv.chat.app.struct.User
import by.bashlikovv.chat.app.utils.SecurityUtilsImpl
import by.bashlikovv.chat.sources.structs.ServerUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

class UsersViewModel(
    private val accountsRepository: AccountsRepository,
    private val messengerViewModel: MessengerViewModel
) : ViewModel() {

    private val usersRepository: UsersRepository = OkHTTPUsersRepository()

    private val _usersUiState: MutableStateFlow<UsersUiState> = MutableStateFlow(UsersUiState())
    var usersUiState: StateFlow<UsersUiState> = _usersUiState.asStateFlow()

    private val _me = MutableStateFlow(User())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val result = suspendCancellableCoroutine<User> { continuation ->
                continuation.invokeOnCancellation { cancel() }

                viewModelScope.launch(Dispatchers.IO) {
                    usersRepository.getUser(accountsRepository.getAccount().first()?.token ?: "")
                }
            }
            _me.update { result }
        }
    }

    fun getUsers(input: String) = viewModelScope.launch(Dispatchers.IO) {
        messengerViewModel.setUpdateVisibility(true)
        val result = suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation { cancel() }

            viewModelScope.launch(Dispatchers.IO) {
                _usersUiState.update { UsersUiState(listOf()) }
                getSearchOutput(input)
                continuation.resumeWith(Result.success(false))
            }
        }
        messengerViewModel.setUpdateVisibility(result)
    }

    private suspend fun getSearchOutput(input: String) {
        val names = messengerViewModel.messengerUiState.value.chats.map { it.user.userName }
        val serverUsers: List<ServerUser>
        try {
            serverUsers = usersRepository.getUsers(_me.value.userToken)
        } catch (e: Exception) {
            processSearchException(e)
            return
        }
        if (input.isEmpty()) {
            processEmptyInput(serverUsers, names)
        } else {
            processCorrectInput(serverUsers, input, names)
        }
    }

    private suspend fun processCorrectInput(
        serverUsers: List<ServerUser>,
        input: String,
        names: List<String>
    ) {
        serverUsers.forEach { serverUser ->
            if (input.length <= serverUser.username.length) {
                val subStr = serverUser.username.subSequence(0, input.length).toString().lowercase()
                if (subStr == input.lowercase() && subStr != "") {
                    val tmp = Chat(
                        user = User(
                            userName = serverUser.username,
                            userToken = SecurityUtilsImpl().bytesToString(serverUser.token),
                            userImage = usersRepository.getUserImage(serverUser.image.decodeToString())
                        ),
                        messages = listOf(Message(value = "")),
                        time = ""
                    )
                    if (!names.contains(tmp.user.userName)) {
                        _usersUiState.update { state ->
                            state.copy(chats = state.chats + tmp)
                        }
                    }
                }
            }
        }
    }

    private suspend fun processEmptyInput(
        serverUsers: List<ServerUser>,
        names: List<String>
    ) {
        serverUsers.forEach { serverUser ->
            val tmp = Chat(
                user = User(
                    userName = serverUser.username,
                    userToken = SecurityUtilsImpl().bytesToString(serverUser.token),
                    userImage = usersRepository.getUserImage(serverUser.image.decodeToString())
                ),
                messages = listOf(Message(value = "")),
                time = ""
            )
            if (!names.contains(tmp.user.userName)) {
                _usersUiState.update { state ->
                    state.copy(chats = state.chats + tmp)
                }
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