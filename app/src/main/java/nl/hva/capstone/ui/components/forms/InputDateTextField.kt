package nl.hva.capstone.ui.components.forms

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputDateTextField(
    icon: ImageVector = Icons.Default.CalendarToday,
    hint: String = "DD MM YYYY",
    textState: MutableState<TextFieldValue>,
    focusRequester: FocusRequester? = null,
    fillMaxWidthFraction: Float = 1f,
    cornerRadius: Dp = 10.dp
) {
    OutlinedTextField(
        value = textState.value,
        onValueChange = { newValue ->
            val rawInput = newValue.text.filter { it.isDigit() }.take(8)

            val builder = StringBuilder()
            for (i in rawInput.indices) {
                builder.append(rawInput[i])
                if ((i == 1 || i == 3) && i != rawInput.lastIndex) {
                    builder.append("-")
                }
            }

            // Determine new cursor position
            val newCursor = builder.length

            textState.value = TextFieldValue(
                text = builder.toString(),
                selection = TextRange(newCursor)
            )
        },
        placeholder = { Text(hint) },
        leadingIcon = { Icon(icon, contentDescription = hint) },
        singleLine = true,
        shape = RoundedCornerShape(cornerRadius),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = Color(0xFFEDEDED),
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth(fillMaxWidthFraction)
            .height(56.dp)
            .then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier),
        textStyle = TextStyle(fontSize = 18.sp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@SuppressLint("NewApi")
fun formatDateForInput(input: String): String {
    return try {
        val date = LocalDate.parse(input)
        "${date.dayOfMonth.toString().padStart(2, '0')}-${date.monthValue.toString().padStart(2, '0')}-${date.year}"
    } catch (e: Exception) {
        ""
    }
}

fun parseInputDateToIso(input: String): String? {
    return try {
        val parts = input.split("-")
        if (parts.size == 3) {
            val day = parts[0].padStart(2, '0')
            val month = parts[1].padStart(2, '0')
            val year = parts[2]
            "$year-$month-$day"
        } else null
    } catch (e: Exception) {
        null
    }
}