package by.bashlikovv.chat.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import by.bashlikovv.chat.model.LogInViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogInView(logInViewModel: LogInViewModel = viewModel()) {
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
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues), verticalArrangement = Arrangement.Center) {
            AnimatedVisibility(
                visible = !logInUiState.isHaveAccount
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    InputField(value = logInUiState.userName) { logInViewModel.onUserNameChange(it) }
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 25.dp), horizontalArrangement = Arrangement.Center) {
                InputField(value = logInUiState.identifier) { logInViewModel.onIdentifierChange(it) }

            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 25.dp), horizontalArrangement = Arrangement.Center) {
                InputField(value = logInUiState.password) { logInViewModel.onPasswordChange(it) }

            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 25.dp), horizontalArrangement = Arrangement.Center) {
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
fun InputField(logInViewModel: LogInViewModel = viewModel(), value: String, onTextChange: (String) -> Unit) {
    val logInUiState by logInViewModel.logInUiState.collectAsState()
    val context = LocalContext.current

    TextField(
        value = value,
        onValueChange = { onTextChange(it) },
        isError = !logInUiState.isIdentifierCorrect,
        placeholder = { Text("User name") },
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.primaryVariant,
            backgroundColor = MaterialTheme.colors.primary,
            cursorColor = MaterialTheme.colors.secondary
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Email
        ),
        keyboardActions = KeyboardActions(onDone = {
            logInViewModel.onCheckInput(context, logInUiState.isHaveAccount)
        }),
        visualTransformation = VisualTransformation.None
    )
}