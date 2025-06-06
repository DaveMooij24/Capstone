package nl.hva.capstone.ui.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import nl.hva.capstone.ui.components.agenda.*
import nl.hva.capstone.ui.components.topbar.*
import nl.hva.capstone.viewModel.*
import java.time.*
import java.time.format.TextStyle
import java.util.*

@SuppressLint("NewApi")
@Composable
fun Agenda(navController: NavController, appointmentViewModel: AppointmentViewModel, clientViewModel: ClientViewModel, serviceViewModel: ServiceViewModel
) {
    var selectedTab by remember { mutableStateOf("Week planning") }

    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedHour by remember { mutableStateOf(0) }
    var selectedMinute by remember { mutableStateOf(0) }


    val appointmentSaved by appointmentViewModel.appointmentSaved.observeAsState(false)
    val errorMessage by appointmentViewModel.error.observeAsState(null)
    val clients by clientViewModel.clientList.observeAsState(emptyList())
    val services by serviceViewModel.serviceList.observeAsState(emptyList())
    val appointments by appointmentViewModel.appointmentList.observeAsState(emptyList())

    var startDate by remember { mutableStateOf(LocalDate.now()) }

    val startDateAsDate = remember(startDate) {
        Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }
    val endDate = remember(startDate) {
        Date.from(startDate.plusDays(6).atTime(23, 59).atZone(ZoneId.systemDefault()).toInstant())
    }
    var showDatePicker by remember { mutableStateOf(false) }

    val locale = Locale("nl")

    val startMonth = startDate.month
        .getDisplayName(TextStyle.FULL, locale)
        .replaceFirstChar { it.titlecase(locale) }

    val endMonth = startDate.plusDays(6).month
        .getDisplayName(TextStyle.FULL, locale)
        .replaceFirstChar { it.titlecase(locale) }
    
    val monthName = if (startMonth != endMonth) {
        "$startMonth / $endMonth"
    } else {
        startMonth
    }


    LaunchedEffect(Unit) {
        clientViewModel.fetchClients()
        serviceViewModel.fetchService()
        appointmentViewModel.fetchAppointmentsBetween(startDateAsDate, endDate)
    }

    LaunchedEffect(appointmentSaved) {
        if (appointmentSaved) {
            showDialog = false
            appointmentViewModel.fetchAppointmentsBetween(startDateAsDate, endDate)
            appointmentViewModel.resetState()
        }
    }

    HomePageLayout(
        activeItemLabel = "Planning",
        navController = navController,
        topBar = {
            TopBar(
                title = monthName,
                actions = listOf(
                    TopBarAction(Icons.Filled.CalendarToday, "Calendar") {
                        showDatePicker = true
                    },
                    TopBarAction(Icons.Filled.Search, "Search") {
                        navController.navigate("search")
                    }
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {

            DayWeekSwitcher(selectedTab) { selectedTab = it }

            CalendarDaysGrid(
                appointments = appointments,
                clients = clients,
                services = services,
                onTimeSlotClick = { date: String, hour: Int, minute: Int ->
                    selectedDate = date
                    selectedHour = hour
                    selectedMinute = minute
                    showDialog = true
                },
                navController = navController,
                startDate = startDate,
                selectedTab = selectedTab)
        }

        if (showDialog) {
            val snappedMinute = (selectedMinute / 15) * 15

            Dialog(
                onClose = { showDialog = false },
                onSave = { appointment ->
                    appointmentViewModel.saveAppointment(appointment)
                },
                defaultDate = selectedDate,
                defaultTime = String.format("%02d:%02d", selectedHour, snappedMinute),
                errorMessage = errorMessage,
                clients = clients,
                services = services
            )
        }

        if (showDatePicker) {
            val context = LocalContext.current
            val today = LocalDate.now()
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    startDate = LocalDate.of(year, month + 1, dayOfMonth)
                    appointmentViewModel.fetchAppointmentsBetween(
                        Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        Date.from(startDate.plusDays(6).atTime(23, 59).atZone(ZoneId.systemDefault()).toInstant())
                    )
                    showDatePicker = false
                },
                startDate.year,
                startDate.monthValue - 1,
                startDate.dayOfMonth
            ).show()
        }

    }
}
