package nl.hva.capstone

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.google.firebase.FirebaseApp
import nl.hva.capstone.ui.components.LoadingOverlay
import nl.hva.capstone.ui.screens.*
import nl.hva.capstone.ui.theme.CapstoneTheme
import nl.hva.capstone.viewModel.*


class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {

            val application = LocalContext.current.applicationContext as Application
            val loadingViewModel: LoadingViewModel = viewModel()

            val appointmentViewModel: AppointmentViewModel = viewModel(
                factory = AppointmentViewModelFactory(application, loadingViewModel)
            )

            val appointmentProductViewModel: AppointmentProductViewModel = viewModel(
                factory = AppointmentProductViewModelFactory(application, loadingViewModel)
            )

            val clientViewModel: ClientViewModel = viewModel(
                factory = ClientViewModelFactory(application, loadingViewModel)
            )

            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(application, loadingViewModel)
            )

            val productViewModel: ProductViewModel = viewModel(
                factory = ProductViewModelFactory(application, loadingViewModel)
            )

            val purchaseViewModel: PurchaseViewModel = viewModel(
                factory = PurchaseViewModelFactory(application, loadingViewModel)
            )

            val serviceViewModel: ServiceViewModel = viewModel(
                factory = ServiceViewModelFactory(application, loadingViewModel)
            )

            val navController = rememberNavController()
            val loading by loadingViewModel.loading.observeAsState(false)

            CapstoneTheme {
                Box {
                    NavHost(navController, startDestination = "login") {
                        composable("login") { LoginScreen(navController = navController, loginViewModel = loginViewModel, loadingViewModel = loadingViewModel) }
                        composable("actief/{appointmentId}/{clientId}") { backStackEntry ->
                            val appointmentId = backStackEntry.arguments?.getString("appointmentId")
                            val clientId = backStackEntry.arguments?.getString("clientId")
                            Active(navController = navController, appointmentId = appointmentId, clientId = clientId,
                                appointmentViewModel = appointmentViewModel,
                                clientViewModel = clientViewModel,
                                serviceViewModel = serviceViewModel,
                                productViewModel = productViewModel,
                                appointmentProductViewModel = appointmentProductViewModel)
                        }
                        composable("actief") { Active(navController = navController, appointmentId = null, clientId = null,
                            appointmentViewModel = appointmentViewModel,
                            clientViewModel = clientViewModel,
                            serviceViewModel = serviceViewModel,
                            productViewModel = productViewModel,
                            appointmentProductViewModel = appointmentProductViewModel)}
                        composable("agenda") { Agenda(navController = navController, appointmentViewModel = appointmentViewModel, clientViewModel = clientViewModel, serviceViewModel = serviceViewModel) }
                        composable("verkopen") { Sales(navController)}
                        composable("inkopen") { Purchases(navController, purchaseViewModel = purchaseViewModel) }
                        composable("klanten") { Clients(navController, clientViewModel = clientViewModel)}
                        composable("producten") { Products(navController, productViewModel = productViewModel)}
                        composable("behandelingen") { Services(navController, serviceViewModel = serviceViewModel) }
                        composable("search") { SearchPage(navController, clientViewModel = clientViewModel, productViewModel = productViewModel, serviceViewModel = serviceViewModel, purchaseViewModel = purchaseViewModel) }
                    }

                    LoadingOverlay(isLoading = loading)
                }
            }
        }
    }
}