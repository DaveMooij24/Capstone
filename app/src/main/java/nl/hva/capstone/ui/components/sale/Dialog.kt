package nl.hva.capstone.ui.components.sale

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.google.firebase.Timestamp
import nl.hva.capstone.data.model.Purchase
import nl.hva.capstone.data.model.Sale
import nl.hva.capstone.data.model.SaleInformation
import nl.hva.capstone.ui.components.forms.*
import nl.hva.capstone.ui.components.popupDialog.PopupDialog
import java.util.Locale

@Composable
fun Dialog(
    sale: Sale? = null,
    saleInformation: List<SaleInformation>,
    onClose: () -> Unit,
    onPrint: (Sale) -> Unit,
    errorMessage: String? = null,
    title: String
) {
    PopupDialog(title = title, onClose = onClose, errorMessage = errorMessage) {
        // Receipt Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Business header
                Text(
                    text = "Kapsalon Mooij",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Text(text = "Braspenningstraat 70", style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Text(text = "1827 JV Alkmaar", style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Text(text = "06 46598233", style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))

                // Sale info
                val dateStr = sale?.dateTime?.toDate()?.let {
                    java.text.SimpleDateFormat("E dd-MM-yyyy HH:mm", java.util.Locale("nl", "NL")).format(it)
                } ?: "Onbekend"

                Text("Datum : $dateStr")
                Text("Medewerker : Sandra")
                Text("Klant : " + sale?.clientName)

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // Items
                var total = 0.0
                saleInformation.forEach {
                    val price = it.price
                    total += price
                    ReceiptRow(label = it.name, value = "%.2f".format(price))
                }

                val tax = total / 109 * 9
                val totalFormatted = "%.2f".format(total)
                val taxFormatted = "%.2f".format(tax)

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                Text("BTW 9% : $taxFormatted", style = MaterialTheme.typography.bodySmall)
                Text("BTW Totaal : $taxFormatted", style = MaterialTheme.typography.bodySmall)
                ReceiptRow(label = "TOTAAL:", value = totalFormatted, bold = true)
                Spacer(modifier = Modifier.height(8.dp))

                ReceiptRow(label = "Per pin voldaan", value = totalFormatted)

                Spacer(modifier = Modifier.height(16.dp))
                Text("Uw volgende afspraak:")
                if (sale != null) {
                    Text(sale.nextAppointmentDate?.toDate()?.let {
                        java.text.SimpleDateFormat("EEEE dd MMMM yyyy 'om' HH:mm 'uur'", Locale("nl", "NL")).format(it)
                    } ?: "Geen toekomsitge afspraak")

                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Bedankt en tot ziens",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "www.kapsalonmooij.nl",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Print button
        if (sale != null) {
            Button(
                onClick = { onPrint(sale) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F4E4D)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Print", color = Color.White)
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (bold) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall
        )
        Text(
            text = value,
            style = if (bold) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall
        )
    }
}
