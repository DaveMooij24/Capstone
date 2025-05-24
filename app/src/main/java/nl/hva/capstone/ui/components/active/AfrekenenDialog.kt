package nl.hva.capstone.ui.components.active

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import nl.hva.capstone.ui.components.forms.FormButton
import nl.hva.capstone.ui.components.forms.InputTextField
import nl.hva.capstone.ui.components.popupDialog.PopupDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AfrekenenDialog(
    onClose: () -> Unit,
    onCheckout: () -> Unit,
    onShowProducts: () -> Unit,
    extraProductsCount: Int,
    totalAmount: String,
    soortAfspraak: MutableState<String>,
    omschrijving: MutableState<String>,
    watIsErGedaan: MutableState<String>
) {
    PopupDialog(
        title = "Afrekenen",
        onClose = onClose
    ) {
        val focusRequester = remember { FocusRequester() }

        InputTextField(
            icon = Icons.Default.Description,
            hint = "Soort afspraak",
            textState = soortAfspraak,
            focusRequester = focusRequester
        )
        Spacer(modifier = Modifier.height(8.dp))

        InputTextField(
            icon = Icons.Default.Description,
            hint = "Omschrijving",
            textState = omschrijving
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = watIsErGedaan.value,
            onValueChange = { watIsErGedaan.value = it },
            placeholder = { Text("Wat is er gebeurd\n• Punt 1\n• Punt 2\n• Punt 3") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0xFFEDEDED),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            ),
            textStyle = TextStyle(fontSize = 18.sp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        FormButton(
            text = "Producten inzien",
            onClick = onShowProducts,
            height = 40.dp,
            modifier = Modifier.fillMaxWidth(1f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(45.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0EFEF))
        ) {
            Text(
                text = "Extra producten: $extraProductsCount",
                color = Color.Black,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 12.dp, top = 12.dp)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(45.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0EFEF))
        ) {
            Text(
                text = "Totaalbedrag: $totalAmount",
                color = Color.Black,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 12.dp, top = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        FormButton(
            text = "Opslaan",
            onClick = onClose,
            height = 40.dp,
            modifier = Modifier.fillMaxWidth(1f)
        )

        Spacer(modifier = Modifier.height(10.dp))


        FormButton(
            text = "Afronden",
            onClick = onCheckout,
            height = 40.dp,
            modifier = Modifier.fillMaxWidth(1f)
        )
    }
}
