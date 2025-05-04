package nl.hva.capstone.ui.components.agenda

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.*
import nl.hva.capstone.data.model.*
import nl.hva.capstone.ui.components.forms.*
import nl.hva.capstone.ui.components.popupDialog.PopupDialog
import java.sql.Timestamp

@Composable
fun Dialog(
    onClose: () -> Unit,
    onSave: (Appointment) -> Unit,
    defaultDate: String = "",
    defaultTime: String = "",
    errorMessage: String? = null,
    clients: List<Client>,
    services: List<Service>

) {
    val selectedClientId = remember { mutableStateOf<Long?>(null) }
    val selectedServiceId = remember { mutableStateOf<Long?>(null) }
    val date = remember { mutableStateOf(TextFieldValue(formatDateForInput(defaultDate))) }
    val time = remember { mutableStateOf(TextFieldValue(defaultTime)) }
    val description = remember { mutableStateOf("") }
    val notes = remember { mutableStateOf("") }

    PopupDialog(title = "Afspraak aanmaken", onClose = onClose, errorMessage = errorMessage) {
        DropdownField(
            items = clients,
            labelSelector = { it.name },
            onItemSelected = { selectedClient -> selectedClientId.value = selectedClient.id },
            hint = "Klant zoeken",
            icon = Icons.Default.Person
        )

        Spacer(modifier = Modifier.height(10.dp))

        DropdownField(
            items = services,
            labelSelector = { it.name },
            onItemSelected = { selectedService -> selectedServiceId.value = selectedService.id },
            hint = "Behandeling zoeken",
            icon = Icons.Filled.ContentCut
        )

        Spacer(modifier = Modifier.height(10.dp))

        InputDateTextField(
            icon = Icons.Filled.CalendarToday,
            hint = "Datum (bv. 01-05-2025)",
            textState = date
        )
        Spacer(modifier = Modifier.height(10.dp))

        InputTimeTextField(
            icon = Icons.Filled.AccessTime,
            hint = "Tijd (bv. 14:30)",
            textState = time
        )
        Spacer(modifier = Modifier.height(10.dp))

        InputTextField(icon = Icons.Filled.Description, hint = "Omschrijving", textState = description)
        Spacer(modifier = Modifier.height(10.dp))

        InputTextField(icon = Icons.Filled.Notes, hint = "Extra inhoud", textState = notes)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                try {
                    val clientId = selectedClientId.value
                    if (clientId == null) throw IllegalArgumentException("Geen klant geselecteerd.")

                    val serviceId = selectedServiceId.value
                    if (serviceId == null) throw IllegalArgumentException("Geen behandeling geselecteerd.")

                    val dateString = parseInputDateToIso(date.value.text)
                    val timeString = time.value.text

                    val fullTimestamp = "$dateString $timeString:00"

                    val appointment = Appointment(
                        id = System.currentTimeMillis(),
                        clientId = clientId,
                        serviceId = serviceId,
                        dateTime = com.google.firebase.Timestamp(Timestamp.valueOf(fullTimestamp)),
                        description = description.value,
                        notes = notes.value
                    )

                    onSave(appointment)

                } catch (e: Exception) {
                    Log.e("AppointmentDialog", "Invalid input: ${e.message}", e)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F4E4D)),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Aanmaken", color = Color.White)
        }
    }
}





