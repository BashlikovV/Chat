package by.bashlikovv.chat

import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore.Images.Media.getBitmap
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import by.bashlikovv.chat.login.LogInView
import by.bashlikovv.chat.login.LogInViewModel
import by.bashlikovv.chat.theme.MessengerTheme
import com.google.firebase.auth.FirebaseAuth

class LogInActivity : ComponentActivity() {
    private val logInViewModel: LogInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val logInUiState by logInViewModel.logInUiState.collectAsState()

            MessengerTheme(darkTheme = true) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            }
                        }
                        logInViewModel.applySuccess()
                    } else {
                        LogInView(logInActivity = this)
                    }

                    if (logInUiState.isSuccess) {
                        val messengerIntent = Intent(this, MessengerActivity::class.java)
                        startActivity(messengerIntent)
                        finish()
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            val uri = data.data ?: return
            val bitmap = getBitmap(contentResolver, uri)
            val bitmapDrawable = BitmapDrawable(Resources.getSystem(), bitmap)
            logInViewModel.applyUserImageUri(uri)
            logInViewModel.applyUserBitmapImage(bitmapDrawable.bitmap.asImageBitmap())
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}