package nl.hva.capstone.ui.components.agenda

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import nl.hva.capstone.data.model.*

@Composable
fun AppointmentBlock(
    appointment: Appointment,
    client: Client,
    navController: NavController
) {
    val start = appointment.dateTime.toDate()
    val end = start.minutes + 30
    val endHour = start.hours + (end / 60)
    val endMinute = end % 60

    Column(
        modifier = Modifier
            .height(54.dp)
            .fillMaxWidth()
            .padding(1.dp)
            .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(6.dp))
            .clickable {
                navController.navigate("actief/${appointment.id}/${client.id}")
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color(0xFF004D40), shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
        )

        Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)) {
            Text(
                text = "${String.format("%02d", start.hours)}:${String.format("%02d", start.minutes)} - " +
                        "${String.format("%02d", endHour)}:${String.format("%02d", endMinute)}",
                fontSize = 10.sp,
                color = Color.Black
            )
            Text(
                text = client.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}
