package nl.hva.capstone.ui.components.purchases

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import nl.hva.capstone.data.model.Purchase
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun Card(
    purchase: Purchase,
    onClick: () -> Unit
) {
    val date = purchase.dateTime.toDate()
    val dateFormatted = if (purchase.dateTime == Timestamp(0, 0)) {
        ""
    } else {
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0EFEF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        if (purchase.image != null) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = purchase.name,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateFormatted,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "€ ${String.format("%.2f", purchase.price ?: 0.0)}",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = "View Icon",
                tint = Color.Black
            )
        }
    }
}

