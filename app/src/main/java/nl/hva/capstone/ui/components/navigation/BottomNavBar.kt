package nl.hva.capstone.ui.components.navigation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BottomNavBar(
    activeItemLabel: String,
    onMenuClick: () -> Unit,
    onItemClick: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color(0xFF40D6C0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 0.dp, end = 88.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavItem(Icons.Filled.CheckCircle, "Actief", activeItemLabel == "Actief") { onItemClick("actief") }
            Dash()
            NavItem(Icons.Filled.DateRange, "Planning", activeItemLabel == "Planning") { onItemClick("agenda") }
            Dash()
            NavItem(Icons.Filled.AccountBox, "Verkopen", activeItemLabel == "Verkopen") { onItemClick("verkopen") }
            Dash()
            NavItem(Icons.Filled.ShoppingCart, "Inkopen", activeItemLabel == "Inkopen") { onItemClick("inkopen") }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = (-25).dp, x = (16).dp)
                .size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )

            FloatingActionButton(
                onClick = onMenuClick,
                containerColor = Color(0xFF40D6C0),
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
            }
        }
    }
}


@Composable
fun NavItem(icon: ImageVector, label: String, isActive: Boolean, onClick: () -> Unit) {
    val tintColor = if (isActive) Color(0xFF29575A) else Color.White
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = tintColor,
            modifier = Modifier.size(35.dp)
        )
        Text(
            text = label,
            color = tintColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun Dash() {
    Canvas(
        modifier = Modifier
            .height(60.dp)
            .width(1.dp)
    ) {
        drawLine(
            color = Color(0xFF29575A),
            start = Offset(0f, 0f),
            end = Offset(0f, size.height),
            strokeWidth = 2f
        )
    }
}

