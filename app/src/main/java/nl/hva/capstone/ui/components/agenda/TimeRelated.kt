package nl.hva.capstone.ui.components.agenda

import android.annotation.SuppressLint
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.hva.capstone.ui.components.agenda.utils.rememberCurrentTimeOffset

@SuppressLint("NewApi")
@Composable
fun HourLabelsColumn(scrollState: ScrollState, isTodayVisible: Boolean) {
    val hours = (7..22).map { String.format("%02d:00", it) }
    val hourHeight = 110.dp
    val offset = rememberCurrentTimeOffset()

    Box(
        modifier = Modifier
            .offset(y = (-5).dp) // Apply negative top spacing
            .verticalScroll(scrollState)
    ) {
        Column {
            hours.forEachIndexed { index, hour ->
                val isLastHour = index == hours.lastIndex
                Column(modifier = Modifier.width(69.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (!isLastHour) {
                        Box(
                            modifier = Modifier.height(hourHeight).fillMaxWidth(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Text(text = hour, fontSize = 10.sp, color = Color.Gray)
                        }
                        Box(
                            modifier = Modifier.height(hourHeight).fillMaxWidth(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Text(text = "${hour.substringBefore(":")}:30", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
                Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color.Black))
            }
        }
        CurrentTimeLineTime(offset, if (isTodayVisible) Color(0xFF40D6C0) else Color(0xFF40D6C0).copy(alpha = 0.6f))
    }
}

@Composable
fun CurrentTimeLineTime(offset: Dp, color: Color) {
    Box(
        modifier = Modifier
            .padding(top = 5.dp + offset)
            .width(70.dp)
            .height(2.dp)
            .background(color)
    )
}

@Composable
fun CurrentTimeLineWithOffset(offset: Dp, color: Color) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = offset)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(color))
    }
}