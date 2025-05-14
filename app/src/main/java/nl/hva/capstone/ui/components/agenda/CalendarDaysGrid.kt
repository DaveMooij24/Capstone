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
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import nl.hva.capstone.data.model.Appointment
import nl.hva.capstone.data.model.Client
import java.time.*
import java.time.format.TextStyle
import java.util.*

@SuppressLint("NewApi")
@Composable
fun CalendarDaysGrid(
    appointments: List<Appointment>,
    clients: List<Client>,
    onTimeSlotClick: (String, Int, Int) -> Unit,
    navController: NavController,
    startDate: LocalDate,
    selectedTab: String
) {
    val today = startDate
    val numberOfDays = if (selectedTab == "Dag planning") 1 else 7
    val days = (0 until numberOfDays).map { offset ->
        val date = today.plusDays(offset.toLong())
        val label = "${date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("nl"))} ${date.dayOfMonth}"
        date to label
    }
    val isSingleDay = numberOfDays == 1


    val isTodayVisible = days.any { it.first == today }
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    LaunchedEffect(isTodayVisible) {
        if (isTodayVisible) {
            val now = LocalTime.now()
            val totalMinutesSinceStart = ((now.hour * 60 + now.minute) - (7 * 60)).coerceAtLeast(0)
            val slotHeightPx = 55
            val scrollOffset = 1400 + totalMinutesSinceStart * (slotHeightPx / 15f)
            verticalScrollState.scrollTo(scrollOffset.toInt())
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.height(55.dp)) {
            Spacer(modifier = Modifier.width(70.dp)) // Space for hour labels
            Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color.Black))

            if (isSingleDay) {
                // Single day: fill the remaining space
                Row(modifier = Modifier.fillMaxWidth()) {
                    CalendarDayHeader(
                        label = days.first().second,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                // Multi-day: scrollable fixed width columns
                Row(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                    days.forEach { (_, label) ->
                        CalendarDayHeader(
                            label = label,
                            modifier = Modifier.width(120.dp)
                        )
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxSize()) {
            HourLabelsColumn(verticalScrollState, isTodayVisible)

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color.Black)
            )

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
}

@SuppressLint("NewApi")
@Composable
fun HourLabelsColumn(scrollState: ScrollState, isTodayVisible: Boolean) {
    val hours = (7..22).map { String.format("%02d:00", it) }
    val hourHeight = 110.dp
    val offset = rememberCurrentTimeOffset()

    Box(
        modifier = Modifier
            .offset(y = (-5).dp) // Apply negative top spacing
            .verticalScroll(scrollState)
    ) {
        Column {
            hours.forEachIndexed { index, hour ->
                val isLastHour = index == hours.lastIndex
                Column(modifier = Modifier.width(69.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.height(hourHeight).fillMaxWidth(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(text = hour, fontSize = 10.sp, color = Color.Gray)
                    }
                    if (!isLastHour) {
                        Box(
                            modifier = Modifier.height(hourHeight).fillMaxWidth(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Text(text = "${hour.substringBefore(":")}:30", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
                Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color.Black))
            }
        }
        CurrentTimeLineTime(offset, if (isTodayVisible) Color(0xFF40D6C0) else Color(0xFF40D6C0).copy(alpha = 0.6f))
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
    val isSingleDay = days.size == 1
    val minutesOfDay = (7 * 60)..(22 * 60) step 15
    val gridHeight = (minutesOfDay.count() * 54).dp

    if (isSingleDay) {
        Column(modifier = Modifier.verticalScroll(verticalScrollState)) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(gridHeight)) {
                Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color.Black))
                CalendarDayColumn(
                    date = days.first().first,
                    appointments = appointments,
                    clients = clients,
                    navController = navController,
                    onTimeSlotClick = onTimeSlotClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .horizontalScroll(horizontalScrollState)
                .verticalScroll(verticalScrollState)
        ) {
            Column {
                Row(modifier = Modifier.height(gridHeight)) {
                    days.forEach { (date, _) ->
                        Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color.Black))
                        CalendarDayColumn(
                            date = date,
                            appointments = appointments,
                            clients = clients,
                            navController = navController,
                            onTimeSlotClick = onTimeSlotClick,
                            modifier = Modifier.width(120.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarDayHeader(label: String, modifier: Modifier = Modifier) {
    Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color.Black))
    Column(
        modifier = modifier
            .height(55.dp)
            .background(Color.White),
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
        Box(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color.Black))
    }
}

@SuppressLint("NewApi")
@Composable
fun CalendarDayColumn(
    date: LocalDate,
    appointments: List<Appointment>,
    clients: List<Client>,
    navController: NavController,
    onTimeSlotClick: (String, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val minutesOfDay = (7 * 60)..(22 * 60) step 15
    val today = remember { LocalDate.now() }
    val isToday = date == today
    val offset = rememberCurrentTimeOffset()

    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
            minutesOfDay.forEach { totalMinutes ->
                val hour = totalMinutes / 60
                val minute = totalMinutes % 60

                val matchedAppointment = appointments.find {
                    val appointmentDateTime = it.dateTime.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
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
                        .clickable { onTimeSlotClick(date.toString(), hour, minute) }
                )

                matchedAppointment?.let { appointment ->
                    clients.find { it.id == appointment.clientId }
                        ?.let { client -> AppointmentBlock(appointment, client, navController) }
                } ?: EmptyTimeSlot(date, hour, minute, onTimeSlotClick)
            }
        }

        CurrentTimeLineWithOffset(offset, if (isToday) Color(0xFF40D6C0) else Color(0xFF40D6C0).copy(alpha = 0.6f))
    }
}

@Composable
fun CurrentTimeLineTime(offset: Dp, color: Color) {
    Box(
        modifier = Modifier
            .padding(top = 5.dp + offset)
            .width(70.dp)
            .height(2.dp)
            .background(color)
    )
}

@Composable
fun CurrentTimeLineWithOffset(offset: Dp, color: Color) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = offset)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(color))
    }
}

@SuppressLint("NewApi")
fun getOffsetFromStartOfDay(): Dp {
    val now = LocalTime.now()
    val minutesFromStart = ((now.hour * 60 + now.minute) - (7 * 60)).coerceAtLeast(0)
    val slotHeightPer15Min = 55f
    return (minutesFromStart / 15f * slotHeightPer15Min).dp
}

@Composable
fun rememberCurrentTimeOffset(): Dp {
    var offset by remember { mutableStateOf(getOffsetFromStartOfDay()) }

    LaunchedEffect(Unit) {
        while (true) {
            offset = getOffsetFromStartOfDay()
            delay(60 * 1000L)
        }
    }
    return offset
}
