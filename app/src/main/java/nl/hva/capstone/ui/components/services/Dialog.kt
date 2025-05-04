package nl.hva.capstone.ui.components.services

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import nl.hva.capstone.data.model.Service
import nl.hva.capstone.ui.components.forms.*
import nl.hva.capstone.ui.components.popupDialog.PopupDialog
import nl.hva.capstone.ui.components.snackbar.SnackbarComponent

@Composable
fun Dialog(
    service: Service? = null,
    onClose: () -> Unit,
    onSave: (Service) -> Unit,
    title: String,
    errorMessage: String? = null // <-- new parameter
) {
    val name = remember { mutableStateOf(service?.name ?: "") }
    val estimatedTimeMinutes = remember { mutableStateOf(service?.estimatedTimeMinutes?.toString() ?: "") }
    val price = remember { mutableStateOf(service?.price?.toString() ?: "") }
    val taxes = remember { mutableStateOf(service?.taxes?.toString() ?: "") }

    PopupDialog(title = title, onClose = onClose, errorMessage = errorMessage) { // pass down
        InputTextField(Icons.Filled.ContentCut, "Naam", textState = name)
        Spacer(modifier = Modifier.height(10.dp))
        InputNumericTextField(
            icon = Icons.Filled.Timer,
            hint = "Duur",
            textState = estimatedTimeMinutes,
            isDouble = false
        )
        Spacer(modifier = Modifier.height(10.dp))
        InputNumericTextField(
            icon = Icons.Filled.AttachMoney,
            hint = "Price",
            textState = price,
            isDouble = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        InputNumericTextField(
            icon = Icons.Filled.Receipt,
            hint = "BTW",
            textState = taxes,
            isDouble = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onSave(
                    Service(
                        id = service?.id ?: System.currentTimeMillis(),
                        name = name.value,
                        estimatedTimeMinutes = estimatedTimeMinutes.value.toIntOrNull(),
                        price = price.value.toDoubleOrNull(),
                        taxes = taxes.value.toIntOrNull()
                    )
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F4E4D)),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Opslaan", color = Color.White)
        }
    }
}
