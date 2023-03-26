package by.bashlikovv.chat.app

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
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
import by.bashlikovv.chat.app.utils.viewModelCreator
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MessengerActivity : ComponentActivity() {
    private lateinit var chatIntent: Intent
    private var data: List<Message>? = null
    private val messengerViewModel by viewModelCreator {
        MessengerViewModel(Repositories.accountsRepository)
    }

    companion object {
        const val DARK_THEME = "dark theme"
        const val CHAT = "chat"
    }

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
                    MessengerView {
                        chatIntent = Intent(applicationContext, ChatActivity::class.java)
                        if (messengerUiState.newChat) {
                            messengerViewModel.onCreateNewChat(it.user)
                        }
                        chatIntent.apply {
                            putExtra(DARK_THEME, messengerUiState.darkTheme)
                            putExtra(
                                CHAT,
                                if (messengerUiState.newChat) {
                                    messengerViewModel.messengerUiState.value.chats.last()
                                } else {
                                    it
                                }
                            )
                        }
                        startActivity(chatIntent)
                    }
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun updateViewData(messengerViewModel: MessengerViewModel) {
        val messengerUiState = messengerViewModel.messengerUiState.value

        GlobalScope.launch {
            messengerViewModel.applyMe(messengerViewModel.getUser())
            data = messengerViewModel.getBookmarks()
            if (data.isNullOrEmpty()) {
                data =  listOf(Message(value = "You do not have bookmarks"))
            }
            val bookmarks = listOf(
                Chat(
                    user = User(userName = "Bookmarks", userImage = UserImage(
                        userImageBitmap = R.drawable.bookmark.getBitmapFromImage(applicationContext)
                    )),
                    messages = data!!
                )
            )
            messengerViewModel.applyMessengerUiState(MessengerUiState(chats = bookmarks))
            if (messengerUiState.darkTheme != Repositories.accountsRepository.isDarkTheme()) {
                messengerViewModel.onThemeChange()
            }
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

    override fun onRestart() {
        updateViewData(messengerViewModel)
        super.onRestart()
    }
}