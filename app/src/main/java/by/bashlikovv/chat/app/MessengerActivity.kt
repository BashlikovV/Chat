package by.bashlikovv.chat.app

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import by.bashlikovv.chat.R
import by.bashlikovv.chat.Repositories
import by.bashlikovv.chat.app.screens.login.UserImage
import by.bashlikovv.chat.app.screens.messenger.MessengerUiState
import by.bashlikovv.chat.app.screens.messenger.MessengerView
import by.bashlikovv.chat.app.screens.messenger.MessengerViewModel
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.struct.Message
import by.bashlikovv.chat.app.struct.User
import by.bashlikovv.chat.app.theme.MessengerTheme
import by.bashlikovv.chat.app.utils.SecurityUtilsImpl
import by.bashlikovv.chat.app.utils.viewModelCreator
import by.bashlikovv.chat.sources.structs.Room
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MessengerActivity : ComponentActivity() {
    private lateinit var chatIntent: Intent
    private var data: List<Message>? = null
    private val messengerViewModel: MessengerViewModel by viewModelCreator {
        MessengerViewModel(Repositories.accountsRepository)
    }

    companion object {
        const val DARK_THEME = "dark theme"
        const val CHAT = "chat"
        const val TOKEN = "token"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Repositories.init(this)
        setContent {
            var updateVisibility by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            fun update() = scope.launch(Dispatchers.IO) {
                updateVisibility = true
                updateViewData(messengerViewModel)
                delay(2000)
                updateVisibility = false
            }
            LaunchedEffect(Unit) { update() }

            val messengerUiState by messengerViewModel.messengerUiState.collectAsState()
            MessengerTheme(darkTheme = messengerUiState.darkTheme) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.primary) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        MessengerView(updateViewData = ::updateViewData) {
                            startActivity(onOpenChat(messengerUiState, it))
                        }
                        if (updateVisibility) { ProgressIndicator() }
                    }
                }
            }
        }
    }

    private fun onOpenChat(messengerUiState: MessengerUiState, it: Chat): Intent {
        chatIntent = Intent(applicationContext, ChatActivity::class.java)
        if (messengerUiState.newChat) {
            messengerViewModel.onCreateNewChat(User(userToken = it.user.userToken))
        }
        chatIntent.apply {
            putExtra(DARK_THEME, messengerUiState.darkTheme)
            var chat = if (messengerUiState.newChat) {
                messengerViewModel.messengerUiState.value.chats.last()
            } else {
                it
            }
            if (chat.user.userName != "Bookmarks") {
                chat = chat.copy(messages = listOf(Message()))
            }
            putExtra(
                CHAT,
                chat.copy(
                    user = chat.user.copy(
                        userImage = chat.user.userImage.copy(
                            userImageBitmap = Bitmap.createBitmap(
                                1, 1, Bitmap.Config.ARGB_8888
                            )
                        )
                    )
                )
            )
            messengerViewModel.viewModelScope.launch {
                putExtra(TOKEN, messengerViewModel.messengerUiState.value.me.userToken)
            }
        }
        return chatIntent
    }

    @Composable
    private fun ProgressIndicator() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colors.secondary
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateViewData(messengerViewModel: MessengerViewModel) {
        val messengerUiState = messengerViewModel.messengerUiState.value

        runBlocking {
            messengerViewModel.applyMe(messengerViewModel.getUser())
            val chats = getBookmarks()
            messengerViewModel.applyMessengerUiState(MessengerUiState(chats = chats))
            if (messengerUiState.darkTheme != Repositories.accountsRepository.isDarkTheme()) {
                messengerViewModel.onThemeChange()
            }
            loadChatsFromServer(messengerViewModel)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun loadChatsFromServer(messengerViewModel: MessengerViewModel) {
        GlobalScope.launch {
            val messengerUiState = messengerViewModel.messengerUiState.value

            val rooms = getRooms()

            messengerViewModel.applyMessengerUiState(
                MessengerUiState(chats = messengerUiState.chats + rooms)
            )
        }
    }

    private suspend fun getBookmarks(): List<Chat> {
        data = messengerViewModel.getBookmarks()
        if (data.isNullOrEmpty()) {
            data =  listOf(Message(value = "You do not have bookmarks"))
        }
        return listOf(
            Chat(
                user = User(userName = "Bookmarks", userImage = UserImage(
                    userImageBitmap = R.drawable.bookmark.getBitmapFromImage(applicationContext)
                )),
                messages = data!!
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getRooms(): List<Chat> {
        val rooms: List<Room>
        try {
            rooms = messengerViewModel.getRooms()
        } catch (e: Exception) {
            return listOf(Chat(messages = listOf(Message(value = "${e.message}")), time = ""))
        }
        return rooms.map {
            val user = if (it.user2.username == messengerViewModel.getUser().userName) {
                it.user1
            } else {
                it.user2
            }
            val messages = messengerViewModel.getMessagesByRoom(room = it)
            val image = UserImage(
                userImageBitmap = messengerViewModel.getImage(user.image.decodeToString()),
                userImageUri = Uri.parse(user.image.decodeToString())
            )
            Chat(
                user = User(
                    userName = user.username,
                    userToken = SecurityUtilsImpl().bytesToString(user.token),
                    userImage = image
                ),
                messages = messages,
                token = SecurityUtilsImpl().bytesToString(it.token)
            )
        }
    }

    private fun Int.getBitmapFromImage(context: Context): Bitmap {
        val db = ContextCompat.getDrawable(context, this)
        val bit = Bitmap.createBitmap(
            db!!.intrinsicWidth, db.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bit)
        db.setBounds(0, 0, canvas.width, canvas.height)
        db.draw(canvas)

        return bit
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRestart() {
        super.onRestart()
        updateViewData(messengerViewModel)
    }
}