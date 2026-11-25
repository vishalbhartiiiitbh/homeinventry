package com.example.homeinvetry.feature.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.homeinvetry.core.ui.StatusBadge
import com.example.homeinvetry.data.sample.StockStatus
import com.example.homeinvetry.ui.theme.*
import com.example.homeinvetry.ui.viewmodel.ItemDetailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    itemId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    itemDetailViewModel: ItemDetailViewModel = viewModel()
) {
    val itemEntity by itemDetailViewModel.getItem(itemId).collectAsState(initial = null)
    val batchesList by itemDetailViewModel.getBatches(itemId).collectAsState(initial = emptyList())
    val transactionsList by itemDetailViewModel.getTransactions(itemId).collectAsState(initial = emptyList())

    var showAddStockDialog by remember { mutableStateOf(false) }
    var showConsumeStockDialog by remember { mutableStateOf(false) }

    val currentItem = itemEntity ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentItem.name, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        itemDetailViewModel.deleteItem(currentItem)
                        onNavigateBack()
                    }) {
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
                .background(AppBackground)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Overview Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                                    text = currentItem.iconEmoji,
                                    style = MaterialTheme.typography.headlineLarge
                                )
                            }
                            Column {
                                Text(text = currentItem.name, style = MaterialTheme.typography.headlineMedium)
                                Text(
                                    text = "Brand: ${currentItem.brand.ifEmpty { "N/A" }}",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText)
                                )
                            }
                        }
                        val stockStatusEnum = try {
                            StockStatus.valueOf(currentItem.status)
                        } catch (e: Exception) {
                            StockStatus.FRESH
                        }
                        StatusBadge(status = stockStatusEnum)
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
                                "${currentItem.quantity} ${currentItem.unit}",
                                style = MaterialTheme.typography.headlineMedium.copy(color = PrimaryGreen, fontWeight = FontWeight.Bold)
                            )
                        }
                        Column {
                            Text("Minimum Stock", style = MaterialTheme.typography.labelMedium.copy(color = SecondaryText))
                            Text("${currentItem.minStock} ${currentItem.unit}", style = MaterialTheme.typography.titleLarge)
                        }
                        Column {
                            Text("Buffer Target", style = MaterialTheme.typography.labelMedium.copy(color = SecondaryText))
                            Text("${currentItem.bufferStock} ${currentItem.unit}", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }

            // Quick Stock Action Buttons (Add Stock, Consume FEFO)
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

                    DetailRow(icon = Icons.Outlined.Place, label = "Storage Location", value = currentItem.storageLocation)
                    if (currentItem.subLocation.isNotEmpty()) {
                        DetailRow(icon = Icons.Outlined.Subtitles, label = "Sub-location", value = currentItem.subLocation)
                    }
                    DetailRow(icon = Icons.Outlined.Category, label = "Category", value = currentItem.category)
                    DetailRow(icon = Icons.Outlined.Event, label = "Earliest Expiry Date", value = currentItem.expiryDate)
                    DetailRow(icon = Icons.Outlined.AttachMoney, label = "Purchase Price", value = "$${currentItem.purchasePrice}")
                    if (currentItem.notes.isNotEmpty()) {
                        DetailRow(icon = Icons.Outlined.Notes, label = "Notes", value = currentItem.notes)
                    }
                }
            }

            // Stock Batches Section (FEFO Tracking) from Room
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
                        "Stock Batches in Room (FEFO Order)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    if (batchesList.isEmpty()) {
                        Text(
                            "Single batch default: ${currentItem.quantity} ${currentItem.unit} (Exp: ${currentItem.expiryDate})",
                            style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText)
                        )
                    } else {
                        batchesList.forEach { batch ->
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

    // Add Stock Dialog with Expiry Date & Price update options
    if (showAddStockDialog) {
        var addQty by remember { mutableStateOf("1.0") }
        var addExpiryDate by remember { mutableStateOf(currentItem.expiryDate) }
        var addPrice by remember { mutableStateOf("0.00") }

        var showAddStockDatePicker by remember { mutableStateOf(false) }
        val addStockDatePickerState = rememberDatePickerState()
        val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

        AlertDialog(
            onDismissRequest = { showAddStockDialog = false },
            title = { Text("Add Stock to ${currentItem.name}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Enter quantity and batch expiry date for newly added stock:", style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText))

                    OutlinedTextField(
                        value = addQty,
                        onValueChange = { addQty = it },
                        label = { Text("Quantity to Add (${currentItem.unit}) *") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = addExpiryDate,
                        onValueChange = { addExpiryDate = it },
                        label = { Text("Batch Expiry Date *") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showAddStockDatePicker = true }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Pick Expiry Date from Calendar", tint = PrimaryGreen)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAddStockDatePicker = true },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = addPrice,
                        onValueChange = { addPrice = it },
                        label = { Text("Purchase Price ($)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val added = addQty.toDoubleOrNull() ?: 0.0
                        val price = addPrice.toDoubleOrNull() ?: 0.0
                        if (added > 0) {
                            itemDetailViewModel.addStock(itemId, added, price, addExpiryDate)
                            showAddStockDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) { Text("Confirm Add & Update Expiry") }
            },
            dismissButton = {
                TextButton(onClick = { showAddStockDialog = false }) { Text("Cancel") }
            }
        )

        // Material 3 DatePicker for Add Stock Dialog
        if (showAddStockDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showAddStockDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        addStockDatePickerState.selectedDateMillis?.let { millis ->
                            addExpiryDate = sdf.format(Date(millis))
                        }
                        showAddStockDatePicker = false
                    }) {
                        Text("Select Date")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddStockDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = addStockDatePickerState)
            }
        }
    }

    // Consume Stock Dialog (FEFO)
    if (showConsumeStockDialog) {
        var consumeQty by remember { mutableStateOf("1.0") }
        AlertDialog(
            onDismissRequest = { showConsumeStockDialog = false },
            title = { Text("Consume Stock (FEFO Order)") },
            text = {
                Column {
                    Text("Consumes stock from the earliest expiring batch first and updates Room database.", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = consumeQty,
                        onValueChange = { consumeQty = it },
                        label = { Text("Quantity consumed (${currentItem.unit})") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val consumed = consumeQty.toDoubleOrNull() ?: 0.0
                        itemDetailViewModel.consumeStockFefo(itemId, consumed)
                        showConsumeStockDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B))
                ) { Text("Confirm FEFO Consume") }
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
