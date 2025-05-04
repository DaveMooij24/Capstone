package nl.hva.capstone.ui.components.clients

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import nl.hva.capstone.data.model.Client
import nl.hva.capstone.ui.components.forms.InputTextField
import nl.hva.capstone.ui.components.popupDialog.PopupDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dialog(
    client: Client? = null,
    onClose: () -> Unit,
    onSave: (Client) -> Unit,
    errorMessage: String? = null,
    title: String
) {
    val name = remember { mutableStateOf(client?.name ?: "") }
    val gender = remember { mutableStateOf(client?.gender ?: "") }
    val phone = remember { mutableStateOf(client?.phone ?: "") }
    val email = remember { mutableStateOf(client?.email ?: "") }
    val color = remember { mutableStateOf(client?.color ?: "") }
    val notes = remember { mutableStateOf(client?.notes ?: "") }

    PopupDialog(title = title, onClose = onClose, errorMessage = errorMessage) {
        InputTextField(Icons.Default.Person, "Naam", textState = name)
        Spacer(modifier = Modifier.height(10.dp))
        InputTextField(Icons.Default.Person, "Geslacht", textState = gender)
        Spacer(modifier = Modifier.height(10.dp))
        InputTextField(Icons.Default.Phone, "Telefoonnummer", textState = phone)
        Spacer(modifier = Modifier.height(10.dp))
        InputTextField(Icons.Default.Email, "Email", textState = email)
        Spacer(modifier = Modifier.height(10.dp))
        InputTextField(Icons.Default.Info, "Kleur", textState = color)
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = notes.value,
            onValueChange = { notes.value = it },
            placeholder = { Text("Bijzonderheden:") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0xFFEDEDED),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onSave(
                    Client(
                        id = client?.id ?: System.currentTimeMillis(),
                        name = name.value,
                        gender = gender.value,
                        phone = phone.value,
                        email = email.value,
                        color = color.value,
                        notes = notes.value
                    )
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F4E4D)),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Opslaan", color = Color.White)
        }

        //TODO set allert for saved or error

    }
}
