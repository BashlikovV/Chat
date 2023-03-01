package by.bashlikovv.chat.login

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogInView(logInViewModel: LogInViewModel = viewModel(), logInActivity: ComponentActivity) {
    val logInUiState by logInViewModel.logInUiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
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
        },
        containerColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.secondary
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(visible = !logInUiState.isHaveAccount) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Image(
                            bitmap = if (logInUiState.userImageBitmap.userImageUrl == "")
                                R.drawable.add_photo.toDrawable().toBitmap(100, 100).asImageBitmap()
                            else
                                logInUiState.userImageBitmap.userImageBitmap.asImageBitmap(),
                            contentDescription = "Your image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(100.dp).clip(RoundedCornerShape(50.dp)).clickable {
                                logInViewModel.selectImage(logInActivity)
                            },
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colors.secondary)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(15.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        InputField(value = logInUiState.userName, text = "User name") {
                            logInViewModel.onUserNameChange(it)
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                InputField(value = logInUiState.identifier, text = "Email") { logInViewModel.onIdentifierChange(it) }

            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                InputField(value = logInUiState.password, text = "Password") { logInViewModel.onPasswordChange(it) }

            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        logInViewModel.onCheckInput(context, logInUiState.isHaveAccount)
                    },
                    content = { Text("LogIn") }
                )
            }
        }
    }
}

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
        onValueChange = { onTextChange(it) },
        isError = !logInUiState.isIdentifierCorrect,
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
            logInViewModel.onCheckInput(context, logInUiState.isHaveAccount)
        })

    )
}