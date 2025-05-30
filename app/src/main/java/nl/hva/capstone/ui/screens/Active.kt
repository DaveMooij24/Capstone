package nl.hva.capstone.ui.screens

import android.util.Log
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import nl.hva.capstone.data.model.*
import nl.hva.capstone.ui.components.forms.*
import nl.hva.capstone.ui.components.active.*
import nl.hva.capstone.ui.components.topbar.*
import nl.hva.capstone.viewModel.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class DialogType {
    object Payment : DialogType()
    object FutureAppointments : DialogType()
    object AppointmentHistory : DialogType()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Active(navController: NavController, appointmentId: String?, clientId: String?) {
    var activeDialog by remember { mutableStateOf<DialogType?>(null) }

    // Existing ViewModel initializations
    val appointmentViewModel: AppointmentViewModel = viewModel()
    val clientViewModel: ClientViewModel = viewModel()
    val serviceViewModel: ServiceViewModel = viewModel()
    val productViewModel: ProductViewModel = viewModel()
    val appointmentProductViewModel: AppointmentProductViewModel = viewModel()


    val products by productViewModel.productList.observeAsState(emptyList())
    val appointmentProducts by appointmentProductViewModel.products.observeAsState(emptyList())
    val client by clientViewModel.client.observeAsState()
    val appointment by appointmentViewModel.appointment.observeAsState()
    val services by serviceViewModel.serviceList.observeAsState(emptyList())

    var initialClientId by remember { mutableStateOf(clientId) }

    LaunchedEffect(appointmentId) {
        if (appointmentId != null) {
            appointmentViewModel.fetchAppointmentsById(appointmentId.toLong())
        } else if (appointmentId == null && clientId == null) {
            appointmentViewModel.fetchMostRecentAppointment()
        }

        serviceViewModel.fetchService()
        productViewModel.fetchProducts()
    }

    LaunchedEffect(appointment) {
        if (appointment != null && initialClientId == null) {
            initialClientId = appointment?.clientId.toString()
        }
        if (appointment != null) {
            appointmentProductViewModel.fetchProductsForAppointment(appointment!!.id)
        }
    }

    LaunchedEffect(initialClientId) {
        initialClientId?.toLongOrNull()?.let {
            clientViewModel.fetchClientById(it)
        }
    }

    val calculatedTotalAmount = remember(appointmentProducts, appointment, services) {
        var subTotal = 0.0
        appointmentProducts.forEach { product ->
            Log.e("price", product.salePrice!!.toString())

            subTotal += product.salePrice
        }
        appointment?.serviceId?.let { currentServiceId ->
            val matchedService = services.find { service -> service.id == currentServiceId }
            matchedService?.let {
                Log.e("price", it.price!!.toString())
                subTotal += it.price
            }
        }
        val dutchLocale = Locale("nl", "NL")
        val currencyFormatter = NumberFormat.getCurrencyInstance(dutchLocale)
        currencyFormatter.format(subTotal)
    }



    HomePageLayout(
        activeItemLabel = "Actief",
        navController = navController,
        topBar = {
            client?.let {
                TopBar(
                    title = it.name,
                    actions = listOf(
                        TopBarAction(Icons.Filled.Edit, "Calendar") {
                                activeDialog = DialogType.Payment
                        },
                        TopBarAction(Icons.Filled.Search, "Search") {
                            navController.navigate("search")
                        }
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            val descriptionState = remember { mutableStateOf("") }
            val notesState = remember { mutableStateOf("") }
            val selectedService = remember { mutableStateOf<String?>(null) }

            Spacer(modifier = Modifier.height(8.dp))

            client?.let { client ->
                appointment?.let { appointment ->
                    val appointmentDate = appointment.dateTime.toDate()
                    val dateFormatted = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(appointmentDate)
                    val timeFormatted = SimpleDateFormat("HH:mm", Locale.getDefault()).format(appointmentDate)
                    val endTimeFormatted = SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                        Date(appointmentDate.time + 30 * 60 * 1000) // assuming 30 min
                    )

                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        InfoRow(icon = Icons.Default.Email, text = client.email)
                        InfoRow(icon = Icons.Default.Phone, text = client.phone)
                        InfoRow(icon = Icons.Default.Person, text = client.gender)
                        InfoRow(icon = Icons.Default.CalendarToday, text = dateFormatted)
                        InfoRow(icon = Icons.Default.Schedule, text = "$timeFormatted - $endTimeFormatted")

                        Spacer(modifier = Modifier.height(16.dp))

                        InputTextField(
                            icon = Icons.Default.Description,
                            hint = "Nieuwe omschrijving",
                            textState = descriptionState
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        DropdownField(
                            items = services,
                            labelSelector = { it.name },
                            onItemSelected = { selectedService.value = it.name },
                            hint = "Soort afspraak",
                            icon = Icons.Default.ArrowDropDown
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = notesState.value,
                            onValueChange = { notesState.value = it },
                            placeholder = { Text("Wat is er gedaan?") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = Color(0xFFEDEDED),
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent
                            ),
                            textStyle = TextStyle(fontSize = 18.sp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        FormButton(text = "Afrekenen", onClick = {
                            activeDialog = DialogType.Payment
                        }, height = 40.dp, modifier = Modifier.fillMaxWidth(1f))

                        Spacer(modifier = Modifier.height(12.dp))

                        FormButton(text = "Toekomstige afspraken", onClick = {
                            activeDialog = DialogType.FutureAppointments
                        }, height = 40.dp, modifier = Modifier.fillMaxWidth(1f))
                        Spacer(modifier = Modifier.height(12.dp))

                        FormButton(text = "Afspraak geschiedenis", onClick = {
                            activeDialog = DialogType.AppointmentHistory
                        }, height = 40.dp, modifier = Modifier.fillMaxWidth(1f))
                    }
                }
            }
        }
        var showCheckout by remember { mutableStateOf(false) }
        var showProductOverview by remember { mutableStateOf(false) }
        var showAddProduct by remember { mutableStateOf(false) }
        var showConfirmPrint by remember { mutableStateOf(false) }
        var showConfirmDelete by remember { mutableStateOf<Product?>(null) }

        var soortAfspraak = remember { mutableStateOf("") }
        var omschrijving = remember { mutableStateOf("") }
        var watIsErGedaan = remember { mutableStateOf("") }
        val selectedProduct = remember { mutableStateOf<Product?>(null) }


        if (activeDialog == DialogType.Payment) {
            showCheckout = true
            activeDialog = null
        }

        // Actual dialogs shown below
        if (showCheckout) {
            AfrekenenDialog(
                onClose = { showCheckout = false },
                onCheckout = { showConfirmPrint = true},
                onShowProducts = { showProductOverview = true },
                extraProductsCount = appointmentProducts.size,
                totalAmount = calculatedTotalAmount,
                soortAfspraak = soortAfspraak,
                omschrijving = omschrijving,
                watIsErGedaan = watIsErGedaan
            )
        }

        if (showProductOverview) {
            ProductenInzichtDialog(
                products = appointmentProducts,
                onClose = { showProductOverview = false },
                onAddClick = { showAddProduct = true },
                onDeleteProduct = { product ->
                    showConfirmDelete = product
                }
            )
        }

        if (showAddProduct) {
            ProductToevoegenDialog(
                products = products,
                selectedProduct = selectedProduct,
                onClose = { showAddProduct = false },
                onAdd = {
                    Log.e("Appointment", selectedProduct.value.toString())
                    selectedProduct.value?.let { selected ->
                        appointment?.let { appointment ->
                            appointmentProductViewModel.addProductToAppointment(
                                appointmentId = appointment.id,
                                productId = selected.id
                            )
                        }
                        selectedProduct.value = null
                        showAddProduct = false
                    }
                }
            )
        }

        if (showConfirmPrint) {
            ConfirmDialog(
                title = "Bon uitprinten",
                onConfirm = {
                    // Implement print logic
                    showConfirmPrint = false
                    appointment?.let { currentAppointment ->
                        appointmentViewModel.updateAppointmentCheckoutStatus(currentAppointment.id, true)
                    }
                },
                onCancel = {
                    showConfirmPrint = false
                    appointment?.let { currentAppointment ->
                        appointmentViewModel.updateAppointmentCheckoutStatus(currentAppointment.id, true)
                    }
                },
            )
        }

        showConfirmDelete?.let { productToConfirmDelete ->
            ConfirmDialog(
                title = "Product verwijderen",
                message = "Weet u zeker dat u '${productToConfirmDelete.name}' wilt verwijderen?",
                onConfirm = {
                    appointment?.let { currentAppointment ->
                        appointmentProductViewModel.deleteProductFromAppointment(
                            appointmentId = currentAppointment.id,
                            productId = productToConfirmDelete.id
                        )
                    }
                    showConfirmDelete = null
                },
                onCancel = {
                    showConfirmDelete = null
                }
            )
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}