package com.example.homeinvetry.core.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.homeinvetry.data.local.entity.InventoryItemEntity
import com.example.homeinvetry.ui.theme.LightGreen
import com.example.homeinvetry.ui.theme.PrimaryGreen
import com.example.homeinvetry.ui.theme.SecondaryText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceConfirmationDialog(
    spokenText: String,
    onDismiss: () -> Unit,
    onConfirmSave: (InventoryItemEntity) -> Unit
) {
    val parsed = remember(spokenText) { VoiceCommandParser.parseAddCommand(spokenText) }

    var name by remember { mutableStateOf(parsed.name) }
    var quantity by remember { mutableStateOf(parsed.quantity.toString()) }
    var unit by remember { mutableStateOf(parsed.unit) }
    var location by remember { mutableStateOf(parsed.location) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Mic, contentDescription = null, tint = PrimaryGreen)
                Text("Voice Item Recognized", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = LightGreen,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🎙️ \"$spokenText\"",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, color = PrimaryGreen),
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Text("Verify or edit parsed details before saving to Room:", style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name *") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Qty") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Storage Location") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val qtyVal = quantity.toDoubleOrNull() ?: 1.0
                        onConfirmSave(
                            InventoryItemEntity(
                                name = name.trim(),
                                quantity = qtyVal,
                                unit = unit.trim(),
                                storageLocation = location.trim(),
                                status = "FRESH"
                            )
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text("Confirm & Save to Room")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
