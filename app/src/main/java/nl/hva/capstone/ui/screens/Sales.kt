package nl.hva.capstone.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun Sales(navController: NavController) {
    HomePageLayout(
        activeItemLabel = "Verkopen",
        navController = navController,
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text("This is the Sales screen")
        }
    }
}




