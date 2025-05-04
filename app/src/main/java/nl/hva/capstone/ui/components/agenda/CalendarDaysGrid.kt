package nl.hva.capstone.ui.components.agenda

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.*
import nl.hva.capstone.data.model.*
import java.time.*
import java.util.Locale
import java.time.format.TextStyle

@SuppressLint("NewApi")
@Composable
fun CalendarDaysGrid(
    appointments: List<Appointment>,
    clients: List<Client>,
    onTimeSlotClick: (String, Int, Int) -> Unit,
    navController: NavController,
    startDate: LocalDate
) {
    val today = startDate
    val days = (0..6).map { offset ->
        val date = today.plusDays(offset.toLong())
        val label = "${date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("nl"))} ${date.dayOfMonth}"
        date to label
    }

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Row(modifier = Modifier.fillMaxSize()) {
        HourLabelsColumn(verticalScrollState)
        CalendarGrid(
            days = days,
            appointments = appointments,
            clients = clients,
            navController = navController,
            onTimeSlotClick = onTimeSlotClick,
            verticalScrollState = verticalScrollState,
            horizontalScrollState = horizontalScrollState
        )
    }
}

@Composable
fun HourLabelsColumn(scrollState: ScrollState) {
    val hours = (7..22).map { String.format("%02d:00", it) }
    val hourHeight = 110.dp

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        Spacer(modifier = Modifier.height(50.dp))
        hours.forEachIndexed { index, hour ->
            val isLastHour = index == hours.lastIndex
            Column(
                modifier = Modifier.width(70.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .height(hourHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(text = hour, fontSize = 10.sp, color = Color.Gray)
                }

                if (!isLastHour) {
                    Box(
                        modifier = Modifier
                            .height(hourHeight)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(text = "${hour.substringBefore(":")}:30", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarGrid(
    days: List<Pair<LocalDate, String>>,
    appointments: List<Appointment>,
    clients: List<Client>,
    navController: NavController,
    onTimeSlotClick: (String, Int, Int) -> Unit,
    verticalScrollState: ScrollState,
    horizontalScrollState: ScrollState
) {
    val minutesOfDay = (7 * 60)..(22 * 60) step 15
    val gridHeight = ((minutesOfDay.count()) * 54.dp)

    Box(
        modifier = Modifier
            .horizontalScroll(horizontalScrollState)
            .verticalScroll(verticalScrollState)
    ) {
        Column {
            CalendarHeaderRow(days)
            Row(modifier = Modifier.height(gridHeight).fillMaxWidth()) {
                days.forEach { (date, _) ->
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(Color.Black)
                    )
                    CalendarDayColumn(
                        date = date,
                        appointments = appointments,
                        clients = clients,
                        navController = navController,
                        onTimeSlotClick = onTimeSlotClick
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarHeaderRow(days: List<Pair<LocalDate, String>>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
    ) {
        days.forEach { (_, label) ->
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color.Black)
            )

            Column(
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(3.dp))
                Box(
                    modifier = Modifier
                        .height(29.dp)
                        .fillMaxWidth()
                        .background(Color(0xFFE0F7FA)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Note", fontSize = 12.sp, color = Color.Gray)
                }
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(Color.Black)
                )
            }
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun CalendarDayColumn(
    date: LocalDate,
    appointments: List<Appointment>,
    clients: List<Client>,
    navController: NavController,
    onTimeSlotClick: (String, Int, Int) -> Unit
) {
    val minutesOfDay = (7 * 60)..(22 * 60) step 15

    Column(
        modifier = Modifier
            .width(120.dp)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        minutesOfDay.forEach { totalMinutes ->
            val hour = totalMinutes / 60
            val minute = totalMinutes % 60

            val matchedAppointment = appointments.find { appointment ->
                val appointmentDateTime = appointment.dateTime.toDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()

                appointmentDateTime.toLocalDate() == date &&
                        appointmentDateTime.hour == hour &&
                        appointmentDateTime.minute == minute
            }

            val lineWidthFraction = when (minute) {
                0 -> 0.75f
                30 -> 0.62f
                else -> 0.5f
            }

            Box(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth(lineWidthFraction)
                    .background(Color(0xFF40D6C0))
                    .clickable {
                        onTimeSlotClick(date.toString(), hour, minute)
                    }
            )

            if (matchedAppointment != null) {
                clients.find { it.id == matchedAppointment.clientId }
                    ?.let { AppointmentBlock(matchedAppointment, it, navController) }
            } else {
                EmptyTimeSlot(date, hour, minute, onTimeSlotClick)
            }
        }
    }
}
