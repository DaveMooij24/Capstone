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
import nl.hva.capstone.viewModel.ProductViewModel

import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import nl.hva.capstone.ui.components.products.*
import nl.hva.capstone.ui.components.topbar.*


@Composable
fun Products(navController: NavController, productViewModel: ProductViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val productList by productViewModel.productList.observeAsState(emptyList())
    val productSaved by productViewModel.productSaved.observeAsState(false)
    val errorMessage by productViewModel.error.observeAsState(null)

    LaunchedEffect(Unit) {
        productViewModel.fetchProducts()
    }

    LaunchedEffect(productSaved) {
        if (productSaved) {
            showDialog = false
            selectedProduct = null
            productViewModel.fetchProducts()
            productViewModel.resetState()
        }
    }

    HomePageLayout(
        activeItemLabel = "",
        navController = navController,
        topBar = {
            TopBar(
                title = "Product Overzicht",
                actions = listOf(
                    TopBarAction(
                        icon = Icons.Default.Add,
                        contentDescription = "Add",
                        onClick = {
                            selectedProduct = null
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
            if (productList.isNotEmpty()) {
                productList.forEach { product ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Card(
                            product = product,
                            onClick = {
                                selectedProduct = product
                                showDialog = true
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                }
            } else {
                Text("Geen producten beschikbaar", modifier = Modifier.padding(16.dp))
            }
        }
    }

    if (showDialog) {
        Dialog(
            product = selectedProduct,
            onClose = {
                showDialog = false
                selectedProduct = null
                productViewModel.resetState()
            },
            onSave = { product ->
                productViewModel.saveProductWithImage(product)
            },
            errorMessage = errorMessage,
            title = if (selectedProduct == null) "Product toevoegen" else "Product bewerken"
        )
    }
}



