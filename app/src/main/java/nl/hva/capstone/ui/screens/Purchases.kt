package nl.hva.capstone.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun Purchases(navController: NavController) {
    HomePageLayout(
        activeItemLabel = "Inkopen",
        navController = navController,
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text("This is the Purchases screen")
        }
    }
}




