package nl.hva.capstone.ui.screens

import android.annotation.SuppressLint
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
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import nl.hva.capstone.data.model.*
import nl.hva.capstone.ui.components.LoadingOverlay
import nl.hva.capstone.ui.components.forms.*
import nl.hva.capstone.ui.components.active.dialogs.*
import nl.hva.capstone.ui.components.topbar.*
import nl.hva.capstone.viewModel.*
import nl.hva.printer.PrinterService
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

sealed class DialogType {
    object Payment : DialogType()
    object FutureAppointments : DialogType()
    object AppointmentHistory : DialogType()
}


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Active(navController: NavController, appointmentId: String?, clientId: String?,
           appointmentViewModel: AppointmentViewModel,
           clientViewModel: ClientViewModel,
           serviceViewModel: ServiceViewModel,
           productViewModel: ProductViewModel,
           appointmentProductViewModel: AppointmentProductViewModel,
           saleViewModel: SaleViewModel
) {
    val context = LocalContext.current

    var activeDialog by remember { mutableStateOf<DialogType?>(null) }

    val appointmentSaved by appointmentViewModel.appointmentSaved.observeAsState(false)
    val products by productViewModel.productList.observeAsState(emptyList())
    val appointmentProducts by appointmentProductViewModel.products.observeAsState(emptyList())
    val appointments by appointmentViewModel.appointmentList.observeAsState(emptyList())

    val client by clientViewModel.client.observeAsState()
    val appointment by appointmentViewModel.appointment.observeAsState()
    val services by serviceViewModel.serviceList.observeAsState(emptyList())

    val description = remember { mutableStateOf("") }
    val notes = remember { mutableStateOf("") }
    val service = remember { mutableStateOf<Service?>(null) }

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

        appointment?.let { appt ->
            description.value = appt.description
            notes.value       = appt.notes
            val match = services.find { it.id == appt.serviceId }
            service.value = match
        }
    }

    LaunchedEffect(initialClientId) {
        initialClientId?.toLongOrNull()?.let {
            clientViewModel.fetchClientById(it)
            appointmentViewModel.fetchAppointmentsForClient(it)
        }
    }

    LaunchedEffect(appointmentSaved) {
        if (appointmentSaved) {
            if (appointmentId != null) {
                appointmentViewModel.fetchAppointmentsById(appointmentId.toLong())
            } else if (appointmentId == null && clientId == null) {
                appointmentViewModel.fetchMostRecentAppointment()
            }

            initialClientId
                ?.toLongOrNull()
                ?.let { clientLong ->
                    appointmentViewModel.fetchAppointmentsForClient(clientLong)
                }
        }
    }

    val calculatedTotalAmount = remember(appointmentProducts, appointment, services) {
        var subTotal = 0.0
        appointmentProducts.forEach { product ->
            subTotal += product.salePrice!!
        }

        service.let {
            subTotal += it.value?.price ?: 0.0
        }

        val dutchLocale = Locale("nl", "NL")
        val currencyFormatter = NumberFormat.getCurrencyInstance(dutchLocale)
        currencyFormatter.format(subTotal)
    }

    fun printReceipt(nextAppointment: Appointment?){
        client?.let { client ->
            service.value?.let { service ->
                val printerService = PrinterService(context)
                val printerIp = "192.168.1.105"
                val currentDate = Date()

                val saleInformationList = mutableListOf<SaleInformation>()

                service.let { serv ->
                    saleInformationList.add(
                        SaleInformation(
                            name = serv.name,
                            price = serv.price ?: 0.0,
                            tax = serv.taxes ?: 0,
                        )
                    )
                }

                appointmentProducts.forEach { product ->
                    saleInformationList.add(
                        SaleInformation(
                            name = product.name,
                            price = product.salePrice ?: 0.0,
                            tax = product.taxes ?: 0,
                        )
                    )
                }

                CoroutineScope(Dispatchers.IO).launch {
                    printerService.printReceipt(
                        printerIp = printerIp,
                        clientName = client.name,
                        appointmentDateTime = currentDate,
                        nextAppointment = nextAppointment?.dateTime?.toDate(),
                        saleInformation = saleInformationList
                    ) { success, message ->
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
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
                        TopBarAction(Icons.Filled.Edit, "Edit") {
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
            Spacer(modifier = Modifier.height(8.dp))

            client?.let { client ->
                appointment?.let { appointment ->
                    val appointmentDate = appointment.dateTime.toDate()
                    val dateFormatted = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(appointmentDate)
                    val timeFormatted = SimpleDateFormat("HH:mm", Locale.getDefault()).format(appointmentDate)
                    val endTimeFormatted = SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                        Date(appointmentDate.time + (service.value?.estimatedTimeMinutes?.times(60) ?: 900) * 1000)
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
                            textState = description,
                            enabled = false
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        InputTextField(
                            icon = Icons.Default.Description,
                            hint = "Soort afspraak",
                            textState = mutableStateOf(service.value?.name.orEmpty()),
                            enabled = false
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = notes.value,
                            onValueChange = { notes.value = it },
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
                            textStyle = TextStyle(fontSize = 18.sp),
                            enabled = false
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
        var showFuture by remember { mutableStateOf(false) }
        var showPast by remember { mutableStateOf(false) }
        var showAppointmentFuture by remember { mutableStateOf(false) }
        var showAppointmentPast by remember { mutableStateOf(false) }

        var showConfirmDeleteAppointment by remember { mutableStateOf<Appointment?>(null) }
        var showConfirmDeleteProduct by remember { mutableStateOf<Product?>(null) }


        val selectedProduct = remember { mutableStateOf<Product?>(null) }
        val selectedAppointment = remember { mutableStateOf<Appointment?>(null) }


        if (activeDialog == DialogType.Payment) {
            showCheckout = true
            activeDialog = null
        }

        if (activeDialog == DialogType.FutureAppointments) {
            showFuture = true
            activeDialog = null
        }

        if (activeDialog == DialogType.AppointmentHistory) {
            showPast = true
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
                soortAfspraak = remember { mutableStateOf(service.value?.name ?: "")},
                omschrijving = description,
                watIsErGedaan = notes
            )
        }

        if(showFuture){
            val futureAppointments = remember(appointments) {
                val currentDate = Date()
                appointments.filter { appointment ->
                    val appointmentDate = (appointment.dateTime as? Timestamp)?.toDate()
                    appointmentDate != null && appointmentDate.after(currentDate)
                }.sortedBy { (it.dateTime as? Timestamp)?.toDate() ?: Date(Long.MAX_VALUE) }
            }

            FutureAppointmentsDialog(
                appointments = futureAppointments,
                onClose = { showFuture = false },
                onViewAppointment = { appointment ->
                    selectedAppointment.value = appointment

                    showAppointmentFuture = true
                    activeDialog = null
                }
            )
        }

        if(showPast){
            val pastAppointments = remember(appointments) {
                val currentDate = Date()
                appointments.filter { appointment ->
                    val appointmentDate =
                        (appointment.dateTime as? Timestamp)?.toDate()
                    appointmentDate != null && appointmentDate.before(currentDate)
                }.sortedByDescending {
                    (it.dateTime as? Timestamp)?.toDate() ?: Date(0)
                }
            }

            PastAppointmentsDialog(
                appointments = pastAppointments,
                onClose = { showPast = false },
                onViewAppointment = { appointment ->
                    selectedAppointment.value = appointment

                    showAppointmentPast = true
                    activeDialog = null
                }
            )
        }

        if (showProductOverview) {
            ProductenInzichtDialog(
                products = appointmentProducts,
                onClose = { showProductOverview = false },
                onAddClick = { showAddProduct = true },
                onDeleteProduct = { product ->
                    showConfirmDeleteProduct = product
                }
            )
        }

        if (showAddProduct) {
            ProductToevoegenDialog(
                products = products,
                selectedProduct = selectedProduct,
                onClose = { showAddProduct = false },
                onAdd = {
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
                    showConfirmPrint = false
                    appointment?.let { currentAppointment ->
                        val currentDate = Date()

                        val nextAppointment = appointments
                            .mapNotNull { appointment ->
                                val date = (appointment.dateTime as? Timestamp)?.toDate()
                                if (date != null && date.after(currentDate)) {
                                    appointment to date
                                } else {
                                    null
                                }
                            }
                            .sortedBy { it.second }
                            .firstOrNull()
                            ?.first

                        CoroutineScope(Dispatchers.IO).launch {
                            client?.let {
                                saleViewModel.processAndSaveSale(
                                    service = service.value,
                                    products = appointmentProducts,
                                    clientName = it.name,
                                    nextAppointment = nextAppointment?.dateTime
                                )
                            }
                            appointmentViewModel.updateAppointmentCheckoutStatus(currentAppointment.id, true)
                            printReceipt(nextAppointment)
                        }
                    }
                },
                onCancel = {
                    showConfirmPrint = false
                    appointment?.let { currentAppointment ->
                        val currentDate = Date()

                        val nextAppointment = appointments
                            .mapNotNull { appointment ->
                                val date = (appointment.dateTime as? Timestamp)?.toDate()
                                if (date != null && date.after(currentDate)) {
                                    appointment to date
                                } else {
                                    null
                                }
                            }
                            .sortedBy { it.second }
                            .firstOrNull()
                            ?.first

                        CoroutineScope(Dispatchers.IO).launch {
                            client?.let {
                                saleViewModel.processAndSaveSale(
                                    service = service.value,
                                    products = appointmentProducts,
                                    clientName = it.name,
                                    nextAppointment = nextAppointment?.dateTime
                                )
                            }
                            appointmentViewModel.updateAppointmentCheckoutStatus(currentAppointment.id, true)
                        }
                    }
                }
            )
        }


        showConfirmDeleteProduct?.let { productToConfirmDelete ->
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
                    showConfirmDeleteProduct = null
                },
                onCancel = {
                    showConfirmDeleteProduct = null
                }
            )
        }

        showConfirmDeleteAppointment?.let { appointmentToConfirmDelete ->
            ConfirmDialog(
                title = "Afspraak verwijderen",
                message = "Weet u zeker dat u de afspraak op '${appointmentToConfirmDelete.dateTime}' wilt verwijderen?",
                onConfirm = {
                    appointmentViewModel.deleteAppointment(appointmentToConfirmDelete)
                    showConfirmDeleteAppointment = null
                },
                onCancel = {
                    showConfirmDeleteAppointment = null
                }
            )
        }

        if(showAppointmentFuture){
            AppointmentInsightDialog(
                onClose = { showAppointmentFuture = false },
                onSave = { updatedAppointment ->
                    appointmentViewModel.saveAppointment(updatedAppointment)
                    showAppointmentFuture = false
                },
                onDelete = { appointment ->
                    showConfirmDeleteAppointment = appointment
                           },
                appointment = selectedAppointment.value,
                service = remember { mutableStateOf(service.value?.name ?: "")}
            )
        }

        if(showAppointmentPast){
            AppointmentInsightDialog(
                onClose = { showAppointmentPast = false },
                onSave = { updatedAppointment ->
                    appointmentViewModel.saveAppointment(updatedAppointment)
                    showAppointmentFuture = false
                },
                onDelete = { appointment ->
                    showConfirmDeleteAppointment = appointment
                },
                appointment = selectedAppointment.value,
                showButtons = false,
                service = remember { mutableStateOf(service.value?.name ?: "")}
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

