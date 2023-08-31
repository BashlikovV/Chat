package by.bashlikovv.messenger.presentation.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewModelScope
import by.bashlikovv.messenger.domain.usecase.CheckSignedInUseCase
import by.bashlikovv.messenger.presentation.view.login.LogInView
import by.bashlikovv.messenger.presentation.view.theme.MessengerTheme
import by.bashlikovv.messenger.presentation.viewmodel.LogInViewModel
import by.bashlikovv.messenger.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LogInActivity : ComponentActivity() {
    private val logInViewModel: LogInViewModel by viewModel<LogInViewModel>()

    private val checkSignedInUseCase: CheckSignedInUseCase by inject<CheckSignedInUseCase>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val logInUiState by logInViewModel.logInUiState.collectAsState()

            LaunchedEffect(Unit) {
                logInViewModel.setProgressVisibility(true)
                val result = suspendCancellableCoroutine {
                    logInViewModel.viewModelScope.launch(Dispatchers.IO) {
                        if (checkSignedInUseCase.execute()) {
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
                        val messengerIntent = Intent(this, MessengerActivity::class.java).apply {
                            action = "your.custom.action"
                        }
                        startActivity(messengerIntent)
                        finish()
                    }
                }
            }
        }
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