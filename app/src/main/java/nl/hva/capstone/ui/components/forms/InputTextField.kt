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
fun InputTextField(
    icon: ImageVector,
    hint: String,
    isPassword: Boolean = false,
    focusRequester: FocusRequester? = null,
    textState: MutableState<String>,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    fillMaxWidthFraction: Float = 1f,
    cornerRadius: Dp = 10.dp
) {
    OutlinedTextField(
        value = textState.value,
        onValueChange = { textState.value = it },
        placeholder = { Text(hint) },
        leadingIcon = { Icon(icon, contentDescription = hint) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
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
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}

