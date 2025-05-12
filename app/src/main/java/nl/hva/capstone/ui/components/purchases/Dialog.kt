package nl.hva.capstone.ui.components.purchases

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.google.firebase.Timestamp
import nl.hva.capstone.data.model.Purchase
import nl.hva.capstone.ui.components.forms.*
import nl.hva.capstone.ui.components.popupDialog.PopupDialog

@Composable
fun Dialog(
    purchase: Purchase? = null,
    onClose: () -> Unit,
    onSave: (Purchase) -> Unit,
    errorMessage: String? = null,
    title: String
) {
    val name = remember { mutableStateOf(purchase?.name ?: "") }

    val salePrice = remember { mutableStateOf(purchase?.price?.toString() ?: "") }
    val taxes = remember { mutableStateOf(purchase?.taxes?.toString() ?: "") }
    val dateTime = remember { mutableStateOf(purchase?.dateTime ?: Timestamp.now()) }

    val image: MutableState<Uri?> = remember { mutableStateOf(purchase?.image) }

    PopupDialog(title = title, onClose = onClose, errorMessage = errorMessage) {
        InputTextField(Icons.Filled.ContentCut, "Purchase naam", textState = name)
        Spacer(modifier = Modifier.height(10.dp))
        InputNumericTextField(Icons.Filled.AttachMoney, "Prijs", textState = salePrice, isDouble = true)
        Spacer(modifier = Modifier.height(10.dp))
        InputNumericTextField(Icons.Filled.Receipt, "Belasting %", textState = taxes, isDouble = false)
        Spacer(modifier = Modifier.height(10.dp))
        InputImagePicker(imageUri = image)
        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                onSave(
                    Purchase(
                        id = purchase?.id ?: System.currentTimeMillis(),
                        name = name.value,
                        price = salePrice.value.toDoubleOrNull(),
                        taxes = taxes.value.toIntOrNull(),
                        dateTime = dateTime.value,
                        image = image.value
                    )
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F4E4D)),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Opslaan", color = Color.White)
        }
    }
}
