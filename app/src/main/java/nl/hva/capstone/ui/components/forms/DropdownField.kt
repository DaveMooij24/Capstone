package nl.hva.capstone.ui.components.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownField(
    items: List<T>,
    labelSelector: (T) -> String,
    onItemSelected: (T) -> Unit,
    hint: String = "Zoeken",
    icon: ImageVector? = null,
    fillMaxWidthFraction: Float = 1f,
    cornerRadius: Dp = 10.dp
) {
    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<T?>(null) }

    val filteredItems = items.filter {
        labelSelector(it).contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxWidth(fillMaxWidthFraction)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                expanded = true
            },
            placeholder = { Text(hint) },
            leadingIcon = {
                icon?.let {
                    Icon(it, contentDescription = hint)
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(cornerRadius),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0xFFEDEDED),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            ),
            textStyle = TextStyle(fontSize = 18.sp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        if (expanded && filteredItems.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 150.dp)
                    .padding(top = 4.dp)
                    .background(Color(0xFFEDEDED)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                LazyColumn {
                    items(filteredItems) { item ->
                        DropdownMenuItem(
                            text = { Text(labelSelector(item)) },
                            onClick = {
                                selectedItem = item
                                searchQuery = labelSelector(item)
                                onItemSelected(item)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
