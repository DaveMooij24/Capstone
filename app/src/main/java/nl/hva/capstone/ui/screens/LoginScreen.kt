package nl.hva.capstone.ui.screens

import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.ImeAction
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.launch

import nl.hva.capstone.ui.components.forms.*
import nl.hva.capstone.ui.components.login.KapsalonBanner
import nl.hva.capstone.utils.*
import nl.hva.capstone.viewmodel.LoginViewModel


@SuppressLint("ContextCastToActivity")
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val username = remember { mutableStateOf("") }
    val savedUsername = remember { mutableStateOf("") }

    val password = remember { mutableStateOf("") }

    val usernameFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    val loginSuccess by viewModel.loginSuccess.observeAsState()
    val errorMessage by viewModel.errorMessage.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    val context = LocalContext.current
    val activity = remember(context) {
        generateSequence(context) { (it as? ContextWrapper)?.baseContext }
            .filterIsInstance<FragmentActivity>()
            .firstOrNull()
    }

    val biometricHelperState = remember { mutableStateOf<BiometricHelper?>(null) }

    val dataStoreManager = remember { DataStoreManager(context) }

    LaunchedEffect(Unit) {
        savedUsername.value = dataStoreManager.getUsername() ?: ""
        username.value = savedUsername.value
    }

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

    LaunchedEffect(activity, savedUsername.value) {
        if (savedUsername.value.isNotEmpty()) {
            activity?.let {
                val helper = BiometricHelper(
                    context = context,
                    activity = it,
                    onAuthSuccess = {
                        scope.launch {
                            viewModel.biometricLogin()
                        }
                    },
                    onAuthError = { error ->
                        Log.e("BiometricHelper", "Auth error: $error")
                    }
                )
                biometricHelperState.value = helper

                if (helper.canAuthenticate()) {
                    helper.showBiometricPrompt()
                }
            }
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

            if (savedUsername.value.isNotEmpty()) {

                biometricHelperState.value?.let { biometricHelper ->
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        IconButton(
                            onClick = { biometricHelper.showBiometricPrompt() },
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    color = Color(0xFF184B4E),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometrisch inloggen",
                                tint = Color.White

                            )
                        }
                    }
                }
            }
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




