package nl.hva.capstone.ui.components.agenda

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import java.time.LocalDate

@Composable
fun EmptyTimeSlot(
    date: LocalDate,
    hour: Int,
    minute: Int,
    onTimeSlotClick: (String, Int, Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .height(54.dp)
                .fillMaxWidth()
                .clickable {
                    onTimeSlotClick(date.toString(), hour, minute)
                }
        )
    }
}
