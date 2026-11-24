package com.example.homeinvetry.feature.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.homeinvetry.core.ui.StatusBadge
import com.example.homeinvetry.data.sample.*
import com.example.homeinvetry.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    itemId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sampleList = remember { SampleDataProvider.sampleInventory }
    var item by remember { mutableStateOf(sampleList.firstOrNull { it.id == itemId } ?: sampleList.first()) }
    var showAddStockDialog by remember { mutableStateOf(false) }
    var showConsumeStockDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item.name, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Edit Action */ }) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit Item")
                    }
                    IconButton(onClick = { /* Delete Action */ }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete Item", tint = Color(0xFFC0392B))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Overview Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(LightGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.name.take(1).uppercase(),
                                    style = MaterialTheme.typography.headlineLarge.copy(color = PrimaryGreenDark)
                                )
                            }
                            Column {
                                Text(text = item.name, style = MaterialTheme.typography.headlineMedium)
                                Text(
                                    text = "Brand: ${item.brand.ifEmpty { "N/A" }}",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText)
                                )
                            }
                        }
                        StatusBadge(status = item.status)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Current Quantity", style = MaterialTheme.typography.labelMedium.copy(color = SecondaryText))
                            Text(
                                "${item.quantity} ${item.unit}",
                                style = MaterialTheme.typography.headlineMedium.copy(color = PrimaryGreen, fontWeight = FontWeight.Bold)
                            )
                        }
                        Column {
                            Text("Minimum Stock", style = MaterialTheme.typography.labelMedium.copy(color = SecondaryText))
                            Text("${item.minStock} ${item.unit}", style = MaterialTheme.typography.titleLarge)
                        }
                        Column {
                            Text("Buffer Target", style = MaterialTheme.typography.labelMedium.copy(color = SecondaryText))
                            Text("${item.bufferStock} ${item.unit}", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }

            // Quick Stock Action Buttons (Add Stock, Consume FEFO, Move Location)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { showAddStockDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Stock")
                }

                Button(
                    onClick = { showConsumeStockDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Consume FEFO")
                }
            }

            // Storage & Expiry Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Storage & Expiry Details", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                    DetailRow(icon = Icons.Outlined.Place, label = "Storage Location", value = item.storageLocation.displayName)
                    if (item.subLocation.isNotEmpty()) {
                        DetailRow(icon = Icons.Outlined.Subtitles, label = "Sub-location", value = item.subLocation)
                    }
                    DetailRow(icon = Icons.Outlined.Category, label = "Category", value = item.category.displayName)
                    DetailRow(icon = Icons.Outlined.Event, label = "Earliest Expiry Date", value = item.expiryDate)
                    DetailRow(icon = Icons.Outlined.AttachMoney, label = "Purchase Price", value = "$${item.purchasePrice}")
                    if (item.notes.isNotEmpty()) {
                        DetailRow(icon = Icons.Outlined.Notes, label = "Notes", value = item.notes)
                    }
                }
            }

            // Stock Batches Section (FEFO FIFO tracking)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Stock Batches (FEFO Tracking)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    if (item.batches.isEmpty()) {
                        Text(
                            "Single batch default: ${item.quantity} ${item.unit} (Exp: ${item.expiryDate})",
                            style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText)
                        )
                    } else {
                        item.batches.forEach { batch ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(batch.batchNumber, style = MaterialTheme.typography.titleSmall)
                                    Text("Purchased: ${batch.purchaseDate}", style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText))
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${batch.quantity} ${batch.unit}", style = MaterialTheme.typography.titleMedium.copy(color = PrimaryGreen))
                                    Text("Expiry: ${batch.expiryDate}", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFC0392B)))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Stock Dialog
    if (showAddStockDialog) {
        var addQty by remember { mutableStateOf("1.0") }
        AlertDialog(
            onDismissRequest = { showAddStockDialog = false },
            title = { Text("Add Stock to ${item.name}") },
            text = {
                OutlinedTextField(
                    value = addQty,
                    onValueChange = { addQty = it },
                    label = { Text("Quantity to add (${item.unit})") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val added = addQty.toDoubleOrNull() ?: 0.0
                        val newQty = item.quantity + added
                        val newStatus = if (newQty > item.minStock) StockStatus.FRESH else item.status
                        item = item.copy(quantity = newQty, status = newStatus)
                        showAddStockDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) { Text("Confirm Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddStockDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Consume Stock Dialog (FEFO)
    if (showConsumeStockDialog) {
        var consumeQty by remember { mutableStateOf("1.0") }
        AlertDialog(
            onDismissRequest = { showConsumeStockDialog = false },
            title = { Text("Consume Stock (FEFO)") },
            text = {
                Column {
                    Text("Consumes stock from the batch with earliest expiry first.", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = consumeQty,
                        onValueChange = { consumeQty = it },
                        label = { Text("Quantity consumed (${item.unit})") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val consumed = consumeQty.toDoubleOrNull() ?: 0.0
                        val newQty = maxOf(0.0, item.quantity - consumed)
                        val newStatus = if (newQty <= item.minStock) StockStatus.LOW_STOCK else item.status
                        item = item.copy(quantity = newQty, status = newStatus)
                        showConsumeStockDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B))
                ) { Text("Confirm Consume") }
            },
            dismissButton = {
                TextButton(onClick = { showConsumeStockDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText))
            Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
        }
    }
}
