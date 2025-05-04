package nl.hva.capstone.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.*
import androidx.compose.ui.text.input.ImeAction

import nl.hva.capstone.ui.components.forms.*
import nl.hva.capstone.ui.components.login.KapsalonBanner
import nl.hva.capstone.viewmodel.LoginViewModel


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val usernameFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    val loginSuccess by viewModel.loginSuccess.observeAsState()
    val errorMessage by viewModel.errorMessage.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.resetLoginState()
        }
    }

    LaunchedEffect(loginSuccess) {
        if (loginSuccess == true) {
            viewModel.resetLoginState()
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF29575A), Color(0xFF48D0BC))
                )
            )
            .padding(top = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KapsalonBanner()
            Spacer(modifier = Modifier.height(32.dp))

            InputTextField(
                icon = Icons.Default.Person,
                hint = "Gebruikersnaam",
                focusRequester = usernameFocusRequester,
                textState = username,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        passwordFocusRequester.requestFocus()
                    }
                ),
                fillMaxWidthFraction = 0.8f,
                cornerRadius = 32.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputTextField(
                icon = Icons.Default.Lock,
                hint = "Wachtwoord",
                focusRequester = passwordFocusRequester,
                isPassword = true,
                textState = password,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.login(username.value, password.value)
                    }
                ),
                fillMaxWidthFraction = 0.8f,
                cornerRadius = 32.dp
            )

            Spacer(modifier = Modifier.height(32.dp))

            FormButton(
                text = "Inloggen",
                onClick = {
                    try {
                        viewModel.login(username.value, password.value)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                maxWidthFraction = 0.8f
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        )
    }

    LaunchedEffect(Unit) {
        usernameFocusRequester.requestFocus()
    }
}




