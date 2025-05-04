package nl.hva.capstone.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import kotlinx.coroutines.*
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import nl.hva.capstone.ui.components.navigation.BottomNavBar
import nl.hva.capstone.ui.components.navigation.SideNavBar
import nl.hva.capstone.ui.components.snackbar.SnackbarComponent
import nl.hva.capstone.viewmodel.ServiceViewModel

@Composable
fun HomePageLayout(
    navController: NavController,
    activeItemLabel: String,
    topBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val scope = rememberCoroutineScope()
    var isSidebarOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = topBar,
            bottomBar = {
                BottomNavBar(
                    activeItemLabel = activeItemLabel,
                    onMenuClick = { isSidebarOpen = true },
                    onItemClick = { route ->
                        navController.navigate(route)
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(top = 0.dp)
                    .fillMaxSize()
            ) {
                content(innerPadding)
            }
        }

        AnimatedVisibility(
            visible = isSidebarOpen,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { isSidebarOpen = false }
            )
        }

        AnimatedVisibility(
            visible = isSidebarOpen,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            ),
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
        ) {
            SideNavBar(navController = navController,
                onLogoutClick = {
                    scope.launch {
                        isSidebarOpen = false
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                })
        }
    }
}



