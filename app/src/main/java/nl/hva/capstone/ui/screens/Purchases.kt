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
import nl.hva.capstone.viewmodel.PurchaseViewModel

import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import nl.hva.capstone.ui.components.purchases.*
import nl.hva.capstone.ui.components.topbar.*


@Composable
fun Purchases(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedPurchase by remember { mutableStateOf<Purchase?>(null) }

    val purchaseViewModel: PurchaseViewModel = viewModel()

    val purchaseList by purchaseViewModel.purchaseList.observeAsState(emptyList())
    val purchaseSaved by purchaseViewModel.purchaseSaved.observeAsState(false)
    val errorMessage by purchaseViewModel.error.observeAsState(null)

    LaunchedEffect(Unit) {
        purchaseViewModel.fetchPurchases()
    }

    LaunchedEffect(purchaseSaved) {
        if (purchaseSaved) {
            showDialog = false
            selectedPurchase = null
            purchaseViewModel.fetchPurchases()
            purchaseViewModel.resetState()
        }
    }

    HomePageLayout(
        activeItemLabel = "",
        navController = navController,
        topBar = {
            TopBar(
                title = "Inkoop Overzicht",
                actions = listOf(
                    TopBarAction(
                        icon = Icons.Default.Add,
                        contentDescription = "Add",
                        onClick = {
                            selectedPurchase = null
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
            if (purchaseList.isNotEmpty()) {
                purchaseList.forEach { purchase ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Card(
                            purchase = purchase,
                            onClick = {
                                selectedPurchase = purchase
                                showDialog = true
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                }
            } else {
                Text("Geen inkopen beschikbaar", modifier = Modifier.padding(16.dp))
            }
        }
    }

    if (showDialog) {
        Dialog(
            purchase = selectedPurchase,
            onClose = {
                showDialog = false
                selectedPurchase = null
                purchaseViewModel.resetState()
            },
            onSave = { purchase ->
                purchaseViewModel.savePurchaseWithImage(purchase)
            },
            errorMessage = errorMessage,
            title = if (selectedPurchase == null) "Inkoop toevoegen" else "Inkoop bewerken"
        )
    }
}



