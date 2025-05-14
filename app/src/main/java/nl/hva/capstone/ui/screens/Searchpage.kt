package nl.hva.capstone.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import nl.hva.capstone.data.model.*
import nl.hva.capstone.viewmodel.*
import nl.hva.capstone.ui.components.clients.Card as ClientCard
import nl.hva.capstone.ui.components.products.Card as ProductCard
import nl.hva.capstone.ui.components.services.Card as ServiceCard
import nl.hva.capstone.ui.components.purchases.Card as PurchaseCard


data class SearchResult(
    val type: String,
    val data: Any
)

@Composable
fun SearchPage(navController: NavController) {
    var query by remember { mutableStateOf(TextFieldValue("")) }

    val clientViewModel: ClientViewModel = viewModel()
    val productViewModel: ProductViewModel = viewModel()
    val serviceViewModel: ServiceViewModel = viewModel()
    val purchaseViewModel: PurchaseViewModel = viewModel()

    val clients by clientViewModel.clientList.observeAsState(emptyList())
    val products by productViewModel.productList.observeAsState(emptyList())
    val services by serviceViewModel.serviceList.observeAsState(emptyList())
    val purchases by purchaseViewModel.purchaseList.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        clientViewModel.fetchClients()
        productViewModel.fetchProducts()
        serviceViewModel.fetchService()
        purchaseViewModel.fetchPurchases()
    }

    val filteredResults = remember(query.text, clients, products, services, purchases) {
        val q = query.text.trim().lowercase()

        val clientResults = clients
            .filter { it.name.contains(q, ignoreCase = true) }
            .map { SearchResult("Klanten", it) }

        val productResults = products
            .filter { it.name.contains(q, ignoreCase = true) }
            .map { SearchResult("Producten", it) }

        val serviceResults = services
            .filter { it.name.contains(q, ignoreCase = true) }
            .map { SearchResult("Behandelingen", it) }

        val purchaseResults = purchases
            .filter { it.name.contains(q, ignoreCase = true) }
            .map { SearchResult("Inkopen", it) }

        (clientResults + productResults + serviceResults + purchaseResults)
            .sortedBy {
                when (val data = it.data) {
                    is Client -> data.name
                    is Product -> data.name
                    is Service -> data.name
                    is Purchase -> data.name
                    else -> ""
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            color = Color(0xFF40D6C0),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Sluit",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                BasicTextField(
                    value = query.text,
                    onValueChange = { query = TextFieldValue(it) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp
                    ),
                    decorationBox = { innerTextField ->
                        if (query.text.isEmpty()) {
                            Text(
                                text = "Zoeken...",
                                color = Color.White.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredResults.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "Geen\nzoekresultaten\ngevonden",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val groupedResults = filteredResults.groupBy { it.type }

                groupedResults.forEach { (type, results) ->

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Gray
                            )
                        }
                    }

                    items(results) { result ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            when (type) {
                                "Klanten" -> {
                                    val client = result.data as? Client
                                    client?.let {
                                        ClientCard(
                                            client = it,
                                            onClick = { /* TODO: Handle click */ }
                                        )
                                    }
                                }

                                "Producten" -> {
                                    val product = result.data as? Product
                                    product?.let {
                                        ProductCard(
                                            product = it,
                                            onClick = { /* TODO: Handle click */ }
                                        )
                                    }
                                }

                                "Behandelingen" -> {
                                    val service = result.data as? Service
                                    service?.let {
                                        ServiceCard(
                                            service = it,
                                            onClick = { /* TODO: Handle click */ }
                                        )
                                    }
                                }

                                "Inkopen" -> {
                                    val purchase = result.data as? Purchase
                                    purchase?.let {
                                        PurchaseCard(
                                            purchase = it,
                                            onClick = { /* TODO: Handle click */ }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

