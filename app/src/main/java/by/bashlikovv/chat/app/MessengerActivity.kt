package by.bashlikovv.chat.app

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.launch

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
            LaunchedEffect(Unit) {
                updateViewData(messengerViewModel)
            }

            val messengerUiState by messengerViewModel.messengerUiState.collectAsState()
            MessengerTheme(darkTheme = messengerUiState.darkTheme) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.primary) {

                    MessengerView(updateViewData = ::updateViewData) {
                        chatIntent = Intent(applicationContext, ChatActivity::class.java)
                        if (messengerUiState.newChat) {
                            messengerViewModel.onCreateNewChat(User(userToken = it.user.userToken))
                        }
                        chatIntent.apply {
                            putExtra(DARK_THEME, messengerUiState.darkTheme)
                            val chat = if (messengerUiState.newChat) {
                                messengerViewModel.messengerUiState.value.chats.last()
                            } else {
                                it
                            }
                            putExtra(CHAT, chat)
                            messengerViewModel.viewModelScope.launch {
                                putExtra(TOKEN, messengerViewModel.messengerUiState.value.me.userToken)
                            }
                        }
                        startActivity(chatIntent)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateViewData(messengerViewModel: MessengerViewModel) {
        val messengerUiState = messengerViewModel.messengerUiState.value

        messengerViewModel.viewModelScope.launch {
            messengerViewModel.applyMe(messengerViewModel.getUser())
            val chats = getBookmarks() + getRooms()
            messengerViewModel.applyMessengerUiState(MessengerUiState(chats = chats))
            if (messengerUiState.darkTheme != Repositories.accountsRepository.isDarkTheme()) {
                messengerViewModel.onThemeChange()
            }
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
        } catch (_: Exception) {
            return listOf(Chat(messages = listOf(Message(value = "Network error.")), time = ""))
        }
        return  rooms.map {
            val user = if (it.user2.username == messengerViewModel.getUser().userName) {
                it.user1
            } else {
                it.user2
            }
            Chat(
                user = User(
                    userName = user.username,
                    userToken = SecurityUtilsImpl().bytesToString(user.token)
                ),
                messages = listOf(Message(value = "", time = "")),
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
        updateViewData(messengerViewModel)
        super.onRestart()
    }
}