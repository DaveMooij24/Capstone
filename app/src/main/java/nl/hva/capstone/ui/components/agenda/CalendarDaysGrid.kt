package nl.hva.capstone.ui.components.agenda

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import nl.hva.capstone.data.model.Appointment
import nl.hva.capstone.data.model.Client
import nl.hva.capstone.data.model.Service
import nl.hva.capstone.ui.components.agenda.utils.rememberCurrentTimeOffset
import java.time.*
import java.time.format.TextStyle
import java.util.*

@SuppressLint("NewApi")
@Composable
fun CalendarDaysGrid(
    appointments: List<Appointment>,
    clients: List<Client>,
    services: List<Service>,
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

    val density = LocalDensity.current


    LaunchedEffect(isTodayVisible) {
        if (isTodayVisible) {
            val now = LocalTime.now()
            val totalMinutesSinceStart = ((now.hour * 60 + now.minute) - (7.75 * 60))
            val slotHeightPerMinuteDp = 3.6f

            // Convert Dp to Px
            val scrollOffsetPx = with(density) {
                (totalMinutesSinceStart * slotHeightPerMinuteDp).dp.toPx()
            }

            verticalScrollState.scrollTo(scrollOffsetPx.toInt())
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
                services = services,
                navController = navController,
                onTimeSlotClick = onTimeSlotClick,
                verticalScrollState = verticalScrollState,
                horizontalScrollState = horizontalScrollState
            )
        }
    }
}

@Composable
fun CalendarGrid(
    days: List<Pair<LocalDate, String>>,
    appointments: List<Appointment>,
    clients: List<Client>,
    services: List<Service>,
    navController: NavController,
    onTimeSlotClick: (String, Int, Int) -> Unit,
    verticalScrollState: ScrollState,
    horizontalScrollState: ScrollState
) {
    val isSingleDay = days.size == 1
    val minutesOfDay = (7 * 60)..(22 * 60) step 15
    val gridHeight = (minutesOfDay.count() * 3.6 * 15).dp

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
                    modifier = Modifier.fillMaxWidth(),
                    services = services
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
                            modifier = Modifier.width(120.dp),
                            services = services
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
    modifier: Modifier = Modifier,
    services: List<Service>
) {
    val startMinuteOfDay = 7 * 60 // 07:00
    val endMinuteOfDay = 22 * 60  // 22:00
    val minuteHeight = 3.6.dp // 15 min * 3.6.dp = 54.dp

    val minutesOfDay = startMinuteOfDay until endMinuteOfDay
    val today = remember { LocalDate.now() }
    val isToday = date == today
    val offset = rememberCurrentTimeOffset()

    val occupiedSlots = mutableSetOf<Pair<Int, Int>>() // hour to minute

    appointments.forEach { appointment ->
        val startTime = appointment.dateTime.toDate()
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()

        if (startTime.toLocalDate() == date) {
            val service = services.find { it.id == appointment.serviceId }
            val duration = service?.estimatedTimeMinutes ?: 15

            repeat(duration+1) { i ->
                val slotTime = startTime.toLocalTime().plusMinutes(i.toLong())

                occupiedSlots.add(slotTime.hour to slotTime.minute)
            }
        }
    }

    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
            minutesOfDay.forEach { minuteOfDay ->
                val hour = minuteOfDay / 60
                val minute = minuteOfDay % 60

                val isOccupied = (hour to minute) in occupiedSlots

                val matchedAppointment = appointments.find {
                    val start = it.dateTime.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                    start.toLocalDate() == date &&
                            start.hour == hour &&
                            start.minute == minute
                }

                when {
                    matchedAppointment != null -> {
                        val client = clients.find { it.id == matchedAppointment.clientId }
                        val service = services.find { it.id == matchedAppointment.serviceId }

                        if (client != null && service != null) {
                            AppointmentBlock(matchedAppointment, client, service, navController)
                        }
                    }
                    !isOccupied -> {
                        EmptyTimeSlot(date, hour, minute, onTimeSlotClick, minuteHeight)
                    }
                }
            }
        }
        CurrentTimeLineWithOffset(offset, if (isToday) Color(0xFF40D6C0) else Color(0xFF40D6C0).copy(alpha = 0.6f))
    }
}