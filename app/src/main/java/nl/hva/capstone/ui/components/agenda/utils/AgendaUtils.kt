package nl.hva.capstone.ui.components.agenda.utils

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.time.LocalTime

@SuppressLint("NewApi")
fun getOffsetFromStartOfDay(): Dp {
    val now = LocalTime.now()
    val minutesFromStart = ((now.hour * 60 + now.minute) - (7 * 60) + 15.5)
    val slotHeightPerMinute = 3.6f
    return (minutesFromStart * slotHeightPerMinute).dp
}

@Composable
fun rememberCurrentTimeOffset(): Dp {
    var offset by remember { mutableStateOf(getOffsetFromStartOfDay()) }

    LaunchedEffect(Unit) {
        while (true) {
            offset = getOffsetFromStartOfDay()
            delay(60 * 1000L)
        }
    }
    return offset
}