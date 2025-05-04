package nl.hva.capstone.ui.components.snackbar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.hva.capstone.ui.components.services.Dialog

@Composable
fun SnackbarComponent(
    errorMessage: String? = null,
    successMessage: String? = null,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage, successMessage) {
        when {
            errorMessage != null -> {
                snackbarHostState.showSnackbar(
                    message = errorMessage,
                    duration = SnackbarDuration.Short
                )
            }
            successMessage != null -> {
                snackbarHostState.showSnackbar(
                    message = successMessage,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    // Wrap SnackbarHost in Box to allow alignment
    Box(modifier = modifier) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter) // This aligns the Snackbar to the top center
                .padding(top = 16.dp) // Optional: adds space from the top of the screen
        )
    }
}
