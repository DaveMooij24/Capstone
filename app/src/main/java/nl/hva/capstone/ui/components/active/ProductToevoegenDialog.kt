package nl.hva.capstone.ui.components.active

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import nl.hva.capstone.data.model.Product
import nl.hva.capstone.ui.components.forms.DropdownField
import nl.hva.capstone.ui.components.forms.FormButton
import nl.hva.capstone.ui.components.popupDialog.PopupDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductToevoegenDialog(
    products: List<Product>,
    selectedProduct: MutableState<Product?>,
    onClose: () -> Unit,
    onAdd: () -> Unit
) {
    PopupDialog(
        title = "Producten toevoegen",
        onClose = onClose
    ) {
        var currentSelectedProduct by remember { mutableStateOf<Product?>(selectedProduct.value) }

        DropdownField(
            items = products,
            labelSelector = { it.name },
            onItemSelected = { product ->
                currentSelectedProduct = product
                selectedProduct.value = product
            },
            hint = "Product zoeken"
        )

        Spacer(modifier = Modifier.height(8.dp))

        FormButton(
            text = "Product toevoegen",
            onClick = {
                if (currentSelectedProduct != null) {
                    selectedProduct.value = currentSelectedProduct
                }
                onAdd()
            },
            modifier = Modifier.fillMaxWidth(1f)
        )
    }
}
