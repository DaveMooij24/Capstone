package nl.hva.capstone.ui.components.active

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import nl.hva.capstone.ui.components.forms.FormButton
import nl.hva.capstone.ui.components.popupDialog.PopupDialog

@Composable
fun ConfirmDialog(
    title: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    message: String? = null
) {
    PopupDialog(
        title = title,
        onClose = onCancel
    ) {
        if (message != null) {
            Text(
                text = message,
                modifier = Modifier.padding(bottom = 16.dp),
                color = Color.White
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            FormButton(
                text = "Ja",
                onClick = onConfirm,
                modifier = Modifier.weight(1f)
            )
            FormButton(
                text = "Nee",
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            )
        }
    }
}