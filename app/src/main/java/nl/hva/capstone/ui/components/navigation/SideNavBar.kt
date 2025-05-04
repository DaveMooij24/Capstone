package nl.hva.capstone.ui.components.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import nl.hva.capstone.R

@Composable
fun SideNavBar(navController: NavController, onLogoutClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(260.dp)
            .fillMaxHeight()
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to Color(0xFF29575A),
                        0.3f to Color(0xFF39938B),
                        1.0f to Color(0xFF48D0BC)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 0.dp),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.kapsalon_mooij_banner_2),
                contentDescription = "Banner 2",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        DrawerItem(
            icon = Icons.Default.Person,
            title = "Klanten",
            onClick = { navController.navigate("klanten") }
        )
        DrawerItem(
            icon = Icons.Default.Info,
            title = "Producten",
            onClick = { navController.navigate("producten") }
        )

        DrawerItem(
            icon = Icons.Default.Settings,
            title = "Behandelingen",
            onClick = { navController.navigate("behandelingen") }
        )

        Spacer(modifier = Modifier.weight(1f))

        DrawerItem(icon = Icons.Default.ExitToApp, title = "Uitloggen", onClick = onLogoutClick)
    }
}

@Composable
fun DrawerItem(icon: ImageVector, title: String, onClick: () -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onClick() }
    ) {
        Icon(icon, contentDescription = title, tint = Color.White)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 18.sp, color = Color.White)
    }
}
