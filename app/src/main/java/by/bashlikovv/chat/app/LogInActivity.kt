package by.bashlikovv.chat.app

import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore.Images.Media.getBitmap
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewModelScope
import by.bashlikovv.chat.R
import by.bashlikovv.chat.Repositories
import by.bashlikovv.chat.app.screens.login.LogInView
import by.bashlikovv.chat.app.screens.login.LogInViewModel
import by.bashlikovv.chat.app.theme.MessengerTheme
import by.bashlikovv.chat.app.utils.viewModelCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

class LogInActivity : ComponentActivity() {
    private val logInViewModel: LogInViewModel by viewModelCreator {
        LogInViewModel(Repositories.accountsRepository)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Repositories.init(this)
        setContent {
            val logInUiState by logInViewModel.logInUiState.collectAsState()

            LaunchedEffect(Unit) {
                logInViewModel.setProgressVisibility(true)
                val result = suspendCancellableCoroutine {
                    logInViewModel.viewModelScope.launch(Dispatchers.IO) {
                        kotlinx.coroutines.delay(1000)
                        if (Repositories.accountsRepository.isSignedIn()) {
                            logInViewModel.applySuccess()
                        }
                        it.resumeWith(Result.success(false))
                    }
                }
                logInViewModel.setProgressVisibility(result)
            }

            MessengerTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (logInUiState.progressBarVisibility) {
                            ProgressIndicator()
                        } else if (!logInUiState.isSuccess) {
                            LogInView()
                        }
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

    @Composable
    private fun ProgressIndicator() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.launcher),
                contentDescription = "Application icon",
                alignment = Alignment.Center,
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colors.background,
                    blendMode = BlendMode.Plus
                )
            )
            LinearProgressIndicator()
        }
    }
}