package nl.hva.capstone.ui.components.forms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputNumericTextField(
    icon: ImageVector,
    hint: String,
    isDouble: Boolean = false,
    focusRequester: FocusRequester? = null,
    textState: MutableState<String>,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    fillMaxWidthFraction: Float = 1f,
    cornerRadius: Dp = 10.dp
) {
    OutlinedTextField(
        value = textState.value,
        onValueChange = { newText ->
            if (newText.isEmpty() || newText.toDoubleOrNull() != null || newText.toIntOrNull() != null) {
                textState.value = newText
            }
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
        keyboardOptions = keyboardOptions.copy(keyboardType = if (isDouble) KeyboardType.Decimal else KeyboardType.Number),
        keyboardActions = keyboardActions
    )
}
