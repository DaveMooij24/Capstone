package nl.hva.capstone.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import nl.hva.capstone.viewModel.SaleViewModel

@Composable
fun Sale(navController: NavController, saleViewModel: SaleViewModel
) {
    HomePageLayout(
        activeItemLabel = "Verkopen",
        navController = navController,
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text("This is the Sales screen")
        }
    }
}




