package by.bashlikovv.chat.app.screens.login

import android.net.Uri
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.R

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogInView(logInViewModel: LogInViewModel = viewModel()) {
    val logInUiState by logInViewModel.logInUiState.collectAsState()
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            logInViewModel.applyUserImage(context, it ?: Uri.EMPTY)
        }
    )

    Scaffold(
        topBar = { TopAppContent() },
        containerColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.secondary,
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(visible = !logInUiState.isHaveAccount) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (logInUiState.userImageBitmap.userImageUrl == "") {
                                DefaultImage(cameraLauncher)
                            } else {
                                UserImage(res = cameraLauncher)
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            InputField(value = logInUiState.userName, text = "User name") {
                                logInViewModel.onUserNameChange(it)
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    InputField(value = logInUiState.identifier, text = "Email") {
                        logInViewModel.onIdentifierChange(it)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    InputField(value = logInUiState.password, text = "Password") {
                        logInViewModel.onPasswordChange(it)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            logInViewModel.onCreateAccountButtonPressed(context)
                        },
                        content = { Text("LogIn") }
                    )
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InputField(
    logInViewModel: LogInViewModel = viewModel(),
    value: String,
    text: String,
    onTextChange: (String) -> Unit
) {
    val logInUiState by logInViewModel.logInUiState.collectAsState()
    val context = LocalContext.current

    TextField(
        value = value,
        onValueChange = {
            if (!logInUiState.isIdentifierCorrect || !logInUiState.isPasswordCorrect) {
                logInViewModel.clearInputErrors()
            }
            onTextChange(it)
        },
        isError = when(text) {
            "Password" -> !logInUiState.isPasswordCorrect
            "Email" -> !logInUiState.isIdentifierCorrect
            else -> false
        },
        placeholder = { Text(text) },
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.primaryVariant,
            backgroundColor = MaterialTheme.colors.primary,
            cursorColor = MaterialTheme.colors.secondary
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = if (text != "Password") ImeAction.Next else ImeAction.Done,
            keyboardType = when(text) {
                "Password" -> KeyboardType.Password
                "Email" -> KeyboardType.Email
                else -> KeyboardType.Text
            }
        ),
        keyboardActions = KeyboardActions(onDone = {
            logInViewModel.onCreateAccountButtonPressed(context)
        })

    )
}

@Composable
fun DefaultImage(res: ManagedActivityResultLauncher<String, Uri?>) {
    Image(
        painter = painterResource(R.drawable.add_photo),
        contentDescription = "Your image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(50.dp))
            .clickable {
                res.launch("image/")
            },
        colorFilter = ColorFilter.tint(color = MaterialTheme.colors.secondary)
    )
}

@Composable
fun UserImage(logInViewModel: LogInViewModel = viewModel(), res: ManagedActivityResultLauncher<String, Uri?>) {
    val logInUiState by logInViewModel.logInUiState.collectAsState()

    Image(
        bitmap = logInUiState.userImageBitmap.userImageBitmap.asImageBitmap(),
        contentDescription = "Your image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(50.dp))
            .clickable {
                res.launch("image/")
            }
    )
}

@Composable
fun TopAppContent(logInViewModel: LogInViewModel = viewModel()) {
    val logInUiState by logInViewModel.logInUiState.collectAsState()

    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { logInViewModel.onButtonClk(false) },
            content = { Text("Do not have account") },
            modifier = Modifier.weight(0.5f),
            border = BorderStroke(
                1.dp,
                if (logInUiState.isHaveAccount)
                    MaterialTheme.colors.primary
                else
                    MaterialTheme.colors.secondary
            )
        )
        Button(
            onClick = { logInViewModel.onButtonClk(true) },
            content = { Text("Have account") },
            modifier = Modifier.weight(0.5f),
            border = BorderStroke(
                1.dp,
                if (logInUiState.isHaveAccount)
                    MaterialTheme.colors.secondary
                else
                    MaterialTheme.colors.primary
            )
        )
    }
}