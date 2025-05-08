package nl.hva.capstone.ui.components.products

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
import nl.hva.capstone.data.model.Product
import nl.hva.capstone.ui.components.forms.*
import nl.hva.capstone.ui.components.popupDialog.PopupDialog

@Composable
fun Dialog(
    product: Product? = null,
    onClose: () -> Unit,
    onSave: (Product) -> Unit,
    errorMessage: String? = null,
    title: String
) {
    val name = remember { mutableStateOf(product?.name ?: "") }

    val salePrice = remember { mutableStateOf(product?.salePrice?.toString() ?: "") }
    val purchasePrice = remember { mutableStateOf(product?.purchasePrice?.toString() ?: "") }
    val taxes = remember { mutableStateOf(product?.taxes?.toString() ?: "") }
    val image: MutableState<Uri?> = remember { mutableStateOf(product?.image) }

    PopupDialog(title = title, onClose = onClose, errorMessage = errorMessage) {
        InputTextField(Icons.Filled.ContentCut, "Product naam", textState = name)
        Spacer(modifier = Modifier.height(10.dp))
        InputNumericTextField(Icons.Filled.AttachMoney, "Verkoop prijs", textState = salePrice, isDouble = true)
        Spacer(modifier = Modifier.height(10.dp))
        InputNumericTextField(Icons.Filled.AttachMoney, "Inkoop prijs", textState = purchasePrice, isDouble = true)
        Spacer(modifier = Modifier.height(10.dp))
        InputNumericTextField(Icons.Filled.Receipt, "Belasting %", textState = taxes, isDouble = false)
        Spacer(modifier = Modifier.height(10.dp))
        InputImagePicker(imageUri = image)
        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                onSave(
                    Product(
                        id = product?.id ?: System.currentTimeMillis(),
                        name = name.value,
                        salePrice = salePrice.value.toDoubleOrNull(),
                        purchasePrice = purchasePrice.value.toDoubleOrNull(),
                        taxes = taxes.value.toIntOrNull(),
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
