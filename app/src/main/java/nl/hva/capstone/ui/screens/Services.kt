package nl.hva.capstone.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import nl.hva.capstone.data.model.*
import nl.hva.capstone.viewModel.ServiceViewModel

import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import nl.hva.capstone.ui.components.services.*
import nl.hva.capstone.ui.components.snackbar.SnackbarComponent
import nl.hva.capstone.ui.components.topbar.*


@Composable
fun Services(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf<Service?>(null) }

    val serviceViewModel: ServiceViewModel = viewModel()

    val serviceList by serviceViewModel.serviceList.observeAsState(emptyList())
    val serviceSaved by serviceViewModel.serviceSaved.observeAsState(false)
    val errorMessage by serviceViewModel.error.observeAsState(null)

    LaunchedEffect(Unit) {
        serviceViewModel.fetchService()
    }

    LaunchedEffect(serviceSaved) {
        if (serviceSaved) {
            showDialog = false
            selectedService = null
            serviceViewModel.fetchService()
            serviceViewModel.resetState()
        }
    }

    HomePageLayout(
        activeItemLabel = "",
        navController = navController,
        topBar = {
            TopBar(
                title = "Behandelingen Overzicht",
                actions = listOf(
                    TopBarAction(
                        icon = Icons.Default.Add,
                        contentDescription = "Add",
                        onClick = {
                            selectedService = null
                            showDialog = true
                        }
                    ),
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
                .verticalScroll(rememberScrollState())
        ) {
            if (serviceSaved) {
                SnackbarComponent(
                    errorMessage = null,
                    successMessage = "Behandeling opgeslagen"
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            if (serviceList.isNotEmpty()) {
                serviceList.forEach { service ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Card(
                            service = service,
                            onClick = {
                                selectedService = service
                                showDialog = true
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                }
            } else {
                Text("Geen behandeling beschikbaar", modifier = Modifier.padding(16.dp))
            }
        }
    }

    if (showDialog) {
        Dialog(
            service = selectedService,
            onClose = {
                showDialog = false
                selectedService = null
                serviceViewModel.resetState()
            },
            onSave = { service ->
                serviceViewModel.saveService(service)
            },
            title = if (selectedService == null) "Dienst toevoegen" else "Dienst bewerken",
            errorMessage = errorMessage
        )
    }
}


