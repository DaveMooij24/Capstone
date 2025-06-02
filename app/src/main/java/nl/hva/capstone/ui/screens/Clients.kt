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
import nl.hva.capstone.viewModel.ClientViewModel

import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import nl.hva.capstone.ui.components.clients.*
import nl.hva.capstone.ui.components.topbar.*


@Composable
fun Clients(navController: NavController, clientViewModel: ClientViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedClient by remember { mutableStateOf<Client?>(null) }

    val clientList by clientViewModel.clientList.observeAsState(emptyList())
    val clientSaved by clientViewModel.clientSaved.observeAsState(false)
    val errorMessage by clientViewModel.error.observeAsState(null)

    LaunchedEffect(Unit) {
        clientViewModel.fetchClients()
    }

    LaunchedEffect(clientSaved) {
        if (clientSaved) {
            showDialog = false
            selectedClient = null
            clientViewModel.fetchClients()
            clientViewModel.resetState()
        }
    }

    HomePageLayout(
        activeItemLabel = "",
        navController = navController,
        topBar = {
            TopBar(
                title = "Klanten Overzicht",
                actions = listOf(
                    TopBarAction(
                        icon = Icons.Default.Add,
                        contentDescription = "Add",
                        onClick = {
                            selectedClient = null
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
        Column(modifier = Modifier
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(3.dp))
            if (clientList.isNotEmpty()) {
                clientList.forEach { client ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Card(
                            client = client,
                            onClick = {
                                selectedClient = client
                                showDialog = true
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                }
            } else {
                Text("Geen klanten beschikbaar", modifier = Modifier.padding(16.dp))
            }
        }
    }

    if (showDialog) {
        Dialog(
            client = selectedClient,
            onClose = {
                showDialog = false
                selectedClient = null
                clientViewModel.resetState()
            },
            onSave = { client ->
                clientViewModel.saveClient(client)
            },
            errorMessage = errorMessage,
            title = if (selectedClient == null) "Klant toevoegen" else "Klant bewerken"
        )
    }
}



