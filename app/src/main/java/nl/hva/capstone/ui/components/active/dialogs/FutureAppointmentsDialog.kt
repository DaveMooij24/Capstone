package nl.hva.capstone.ui.components.active.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import nl.hva.capstone.data.model.Appointment
import nl.hva.capstone.ui.components.active.AppointmentCard
import nl.hva.capstone.ui.components.popupDialog.PopupDialog

@Composable
fun FutureAppointmentsDialog(
    appointments: List<Appointment>,
    onClose: () -> Unit,
    onViewAppointment: (Appointment) -> Unit,
    errorMessage: String? = null
) {
    PopupDialog(
        title = "Toekomstige afspraken",
        onClose = onClose,
        errorMessage = errorMessage
    ) {
        if (appointments.isEmpty()) {
            Text(
                "Geen toekomstige afspraken gevonden.",
                color = Color.White,
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .padding(vertical = 8.dp)
            ) {
                items(appointments) { appointment ->
                    AppointmentCard (
                        appointment = appointment,
                        onViewClick = { onViewAppointment(appointment) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
