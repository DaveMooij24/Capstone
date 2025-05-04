package nl.hva.capstone.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import nl.hva.capstone.ui.components.topbar.*
import nl.hva.capstone.viewmodel.*

@Composable
fun Active(navController: NavController, appointmentId: String?, clientId: String?) {
    val appointmentViewModel: AppointmentViewModel = viewModel()
    val clientViewModel: ClientViewModel = viewModel()
    val serviceViewModel: ServiceViewModel = viewModel()

    val client by clientViewModel.client.observeAsState()
    val appointment by appointmentViewModel.appointment.observeAsState()

    var initialClientId by remember { mutableStateOf(clientId) }

    LaunchedEffect(appointmentId) {
        if (appointmentId != null) {
            appointmentViewModel.fetchAppointmentsById(appointmentId.toLong())
        } else if (appointmentId == null && clientId == null) {
            appointmentViewModel.fetchMostRecentAppointment()
        }
    }

    LaunchedEffect(appointment) {
        if (appointment != null && initialClientId == null) {
            initialClientId = appointment?.clientId.toString()
        }
    }

    LaunchedEffect(initialClientId) {
        initialClientId?.toLongOrNull()?.let {
            clientViewModel.fetchClientById(it)
        }
    }

    HomePageLayout(
        activeItemLabel = "Actief",
        navController = navController,
        topBar = {
            client?.let {
                TopBar(
                    title = it.name,
                    actions = listOf(
                        TopBarAction(Icons.Filled.CalendarToday, "Calendar") {},
                        TopBarAction(Icons.Filled.Search, "Search") {}
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text("This is the Active screen")
        }
    }
}




