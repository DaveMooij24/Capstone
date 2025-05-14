package nl.hva.capstone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.*
import com.google.firebase.FirebaseApp
import nl.hva.capstone.ui.screens.*
import nl.hva.capstone.ui.theme.CapstoneTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            val navController = rememberNavController()

            NavHost(navController, startDestination = "login") {
                composable("login") { LoginScreen(onLoginSuccess = { navController.navigate("agenda") }) }
                composable("actief/{appointmentId}/{clientId}") { backStackEntry ->
                    val appointmentId = backStackEntry.arguments?.getString("appointmentId")
                    val clientId = backStackEntry.arguments?.getString("clientId")
                    Active(navController = navController, appointmentId = appointmentId, clientId = clientId)
                }
                composable("actief") { Active(navController = navController, appointmentId = null, clientId = null)}
                composable("agenda") { Agenda(navController) }
                composable("verkopen") { Sales(navController)}
                composable("inkopen") { Purchases(navController) }

                composable("klanten") { Clients(navController)}
                composable("producten") { Products(navController)}
                composable("behandelingen") { Services(navController) }
            }
        }
    }
}