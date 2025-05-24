package nl.hva.capstone.ui.components.agenda

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import java.time.LocalDate

@SuppressLint("SuspiciousIndentation")
@Composable
fun EmptyTimeSlot(
    date: LocalDate,
    hour: Int,
    minute: Int,
    onTimeSlotClick: (String, Int, Int) -> Unit,
    minuteHeight: Dp
) {
    val isQuarter = minute % 15 == 0
    val adjustedHeight = if (isQuarter) minuteHeight - 1.dp else minuteHeight

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            if (isQuarter) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(
                            when (minute) {
                                30 -> 0.62f
                                0 -> 0.75f
                                else -> 0.5f
                            }
                        )
                        .height(1.dp)
                        .background(Color(0xFF40D6C0))
                )
            }

            Box(
                modifier = Modifier
                    .height(adjustedHeight)
                    .fillMaxWidth()
                    .clickable {
                        onTimeSlotClick(date.toString(), hour, minute)
                    }
            )
        }

}
