package nl.hva.capstone.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.hva.capstone.data.model.Purchase
import nl.hva.capstone.data.model.Sale
import nl.hva.capstone.ui.components.sale.Dialog
import nl.hva.capstone.ui.components.topbar.TopBar
import nl.hva.capstone.ui.components.topbar.TopBarAction
import nl.hva.capstone.viewModel.PurchaseViewModel
import nl.hva.capstone.viewModel.SaleViewModel
import nl.hva.printer.PrinterService
import java.util.Date

@Composable
fun Sale(navController: NavController, saleViewModel: SaleViewModel
) {
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var selectedSale by remember { mutableStateOf<Sale?>(null) }

    val sales by saleViewModel.sales.observeAsState(emptyList())
    val saleInformation by saleViewModel.saleInformation.observeAsState(emptyList())
    val errorMessage by saleViewModel.error.observeAsState(null)

    LaunchedEffect(Unit) {
        saleViewModel.fetchSales()
    }

    LaunchedEffect(showDialog, selectedSale) {
        if (showDialog && selectedSale != null) {
            saleViewModel.fetchSaleInformation(selectedSale!!)
        }
    }

    HomePageLayout(
        activeItemLabel = "",
        navController = navController,
        topBar = {
            TopBar(
                title = "Verkoop Overzicht",
                actions = listOf(
//                    TopBarAction(
//                        icon = Icons.Default.Add,
//                        contentDescription = "Add",
//                        onClick = {
//                            selectedSale = null
//                            showDialog = true
//                        }
//                    ),
//                    TopBarAction(Icons.Filled.Search, "Search") {
//                        navController.navigate("search")
//                    }
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(3.dp))
            if (sales.isNotEmpty()) {
                sales.forEach { sale ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        nl.hva.capstone.ui.components.sale.Card(
                            sale = sale,
                            onClick = {
                                selectedSale = sale
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
            sale = selectedSale,
            saleInformation = saleInformation,
            onClose = {
                showDialog = false
                selectedSale = null
                saleViewModel.resetState()
            },
            onPrint = {
                val printerService = PrinterService(context)
                val printerIp = "192.168.1.105"
                CoroutineScope(Dispatchers.IO).launch {
                    selectedSale?.dateTime?.let { it1 ->
                        printerService.printReceipt(
                            printerIp = printerIp,
                            clientName = selectedSale!!.clientName,
                            appointmentDateTime = it1.toDate(),
                            saleInformation = saleInformation,
                            nextAppointment = selectedSale!!.nextAppointmentDate?.toDate()
                        ) { success, message ->
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            },
            errorMessage = errorMessage,
            title = "Bon"
        )
    }
}




