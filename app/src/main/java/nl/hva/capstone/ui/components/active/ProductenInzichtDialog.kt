package nl.hva.capstone.ui.components.active
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import nl.hva.capstone.data.model.Product
import nl.hva.capstone.ui.components.forms.FormButton
import nl.hva.capstone.ui.components.popupDialog.PopupDialog

@Composable
fun ProductenInzichtDialog(
    products: List<Product>,
    onClose: () -> Unit,
    onAddClick: () -> Unit,
    onDeleteProduct: (Product) -> Unit
) {
    PopupDialog(
        title = "Producten inzicht",
        onClose = onClose
    ) {
        FormButton(
            text = "+",
            onClick = onAddClick,
            height = 40.dp,
            modifier = Modifier.fillMaxWidth(1f)
        )
        Spacer(modifier = Modifier.height(8.dp))

        products.forEach { product ->
            ProductCard(
                product = product,
                onDelete = {
                onDeleteProduct(product)
                     }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        FormButton(
            text = "Opslaan",
            onClick = onClose,
            height = 40.dp,
            modifier = Modifier.fillMaxWidth(1f)
        )
    }
}
