package nl.hva.capstone.ui.components.agenda

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*

@Composable
fun DayWeekSwitcher(selectedTab: String, onTabSelected: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E0E0))
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        listOf("Dag planning", "Week planning").forEach { label ->
            Button(
                onClick = { onTabSelected(label) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == label) Color(0xFF40D6C0) else Color.LightGray
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(label, color = Color.White)
            }
        }
    }
}
