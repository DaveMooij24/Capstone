package nl.hva.capstone.ui.components.active.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextStyle
import com.google.firebase.Timestamp
import nl.hva.capstone.data.model.Appointment
import nl.hva.capstone.ui.components.forms.FormButton
import nl.hva.capstone.ui.components.forms.InputTextField
import nl.hva.capstone.ui.components.popupDialog.PopupDialog
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentInsightDialog(
    onClose: () -> Unit,
    onSave: (Appointment) -> Unit,
    onDelete: (Appointment) -> Unit,
    appointment: Appointment?,
    showButtons: Boolean = true
) {
    val dateTime = remember { mutableStateOf(appointment?.dateTime.toString() ?: "") }
    val serviceId = remember { mutableStateOf(appointment?.serviceId.toString() ?: "") }
    val description = remember { mutableStateOf(appointment?.description ?: "") }
    val notes = remember { mutableStateOf(appointment?.notes ?: "") }

    PopupDialog(
        title = "Afspraak van $dateTime",
        onClose = onClose
    ) {
        InputTextField(
            icon = Icons.Default.Description,
            hint = "Soort afspraak",
            textState = serviceId,
        )
        Spacer(modifier = Modifier.height(8.dp))

        InputTextField(
            icon = Icons.Default.Description,
            hint = "Omschrijving",
            textState = description
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = notes.value,
            onValueChange = { notes.value = it },
            placeholder = { Text("Wat is er gebeurd\n• Punt 1\n• Punt 2\n• Punt 3") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0xFFEDEDED),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            ),
            textStyle = TextStyle(fontSize = 18.sp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if(showButtons) {
            FormButton(
                text = "Verwijderen",
                onClick = {
                    appointment?.let { onDelete(it) }
                },
                height = 40.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            FormButton(
                text = "Opslaan",
                onClick = {
                    val updatedAppointment = appointment?.copy(
                        serviceId = serviceId.value.toLongOrNull() ?: 0L,
                        description = description.value,
                        notes = notes.value
                    )
                    updatedAppointment?.let { onSave(it) }
                },
                height = 40.dp,
                modifier = Modifier.fillMaxWidth(1f)
            )
        }
    }
}
