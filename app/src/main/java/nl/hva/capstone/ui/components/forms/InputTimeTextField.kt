package nl.hva.capstone.ui.components.forms

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputTimeTextField(
    icon: ImageVector,
    hint: String,
    textState: MutableState<TextFieldValue>,
    focusRequester: FocusRequester? = null,
    fillMaxWidthFraction: Float = 1f,
    cornerRadius: Dp = 10.dp
) {
    OutlinedTextField(
        value = textState.value,
        onValueChange = { newValue ->
            val digitsOnly = newValue.text.filter { it.isDigit() }
            val formatted = when {
                digitsOnly.length >= 3 -> digitsOnly.take(2) + ":" + digitsOnly.drop(2).take(2)
                else -> digitsOnly
            }

            val newCursor = formatted.length
            textState.value = TextFieldValue(
                text = formatted,
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
