package nl.hva.capstone.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import nl.hva.capstone.ui.components.agenda.*
import nl.hva.capstone.ui.components.topbar.*
import nl.hva.capstone.viewmodel.*
import java.time.*
import java.time.format.TextStyle
import java.util.*

@SuppressLint("NewApi")
@Composable
fun WeekAgenda(navController: NavController) {
    var selectedTab by remember { mutableStateOf("Week planning") }

    val appointmentViewModel: AppointmentViewModel = viewModel()
    val clientViewModel: ClientViewModel = viewModel()
    val serviceViewModel: ServiceViewModel = viewModel()

    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedHour by remember { mutableStateOf(0) }
    var selectedMinute by remember { mutableStateOf(0) }


    val appointmentSaved by appointmentViewModel.appointmentSaved.observeAsState(false)
    val errorMessage by appointmentViewModel.error.observeAsState(null)
    val clients by clientViewModel.clientList.observeAsState(emptyList())
    val services by serviceViewModel.serviceList.observeAsState(emptyList())
    val appointments by appointmentViewModel.appointmentList.observeAsState(emptyList())

    val today = remember { LocalDate.now() }
    val startDate = today
    val startDateAsDate =  remember {
        Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }
    val endDate = remember { Date.from(startDate.plusDays(6).atTime(23, 59).atZone(ZoneId.systemDefault()).toInstant()) }
    val monthName = startDateAsDate.toInstant()
        .atZone(ZoneId.systemDefault())
        .month
        .getDisplayName(TextStyle.FULL, Locale("nl"))

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
                    TopBarAction(Icons.Filled.CalendarToday, "Calendar") {},
                    TopBarAction(Icons.Filled.Search, "Search") {}
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
            Dialog(
                onClose = { showDialog = false },
                onSave = { appointment ->
                    appointmentViewModel.saveAppointment(appointment)
                },
                defaultDate = selectedDate,
                defaultTime = String.format("%02d:%02d", selectedHour, selectedMinute),
                errorMessage = errorMessage,
                clients = clients,
                services = services
            )
        }
    }
}
