package nl.hva.capstone.ui.components.agenda

import android.annotation.SuppressLint
import android.util.Log
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
import java.time.ZoneId

@SuppressLint("NewApi")
@Composable
fun AppointmentBlock(
    appointment: Appointment,
    client: Client,
    service: Service,
    navController: NavController
) {
    val durationMinutes = service.estimatedTimeMinutes ?: 15
    val heightDp = ((durationMinutes+1.5) * 3.6f).dp

    val start = appointment.dateTime.toDate()
    val endTime = start.toInstant().plusSeconds(durationMinutes * 60L)
    val endLocal = endTime.atZone(ZoneId.systemDefault()).toLocalTime()

    Column(
        modifier = Modifier
            .height(heightDp)
            .fillMaxWidth()
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
                        "${String.format("%02d", endLocal.hour)}:${String.format("%02d", endLocal.minute)}",
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

