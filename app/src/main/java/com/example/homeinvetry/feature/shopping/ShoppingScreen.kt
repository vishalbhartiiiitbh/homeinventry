package com.example.homeinvetry.feature.shopping

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.homeinvetry.data.sample.*
import com.example.homeinvetry.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val shoppingList = remember { SampleDataProvider.sampleShoppingList.toMutableStateList() }
    var selectedFilter by remember { mutableStateOf("Pending") } // "All", "Pending", "Purchased"
    var showAddItemDialog by remember { mutableStateOf(false) }
    var purchaseDialogItem by remember { mutableStateOf<ShoppingItemSample?>(null) }

    val pendingItems = shoppingList.filter { !it.isPurchased }
    val purchasedItems = shoppingList.filter { it.isPurchased }
    val estimatedTotalCost = pendingItems.sumOf { it.estimatedPrice * it.requiredQuantity }

    val displayedItems = when (selectedFilter) {
        "Pending" -> pendingItems
        "Purchased" -> purchasedItems
        else -> shoppingList
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddItemDialog = true },
                containerColor = PrimaryGreen,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Shopping Item")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text("Shopping List", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Auto-generated from low stock & manual items",
                    style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText)
                )
            }

            // Summary Stats Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = LightBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Pending Items", style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF1E3246)))
                        Text(
                            "${pendingItems.size} items",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E3246))
                        )
                    }
                    Divider(
                        modifier = Modifier
                            .height(36.dp)
                            .width(1.dp),
                        color = BorderOutline
                    )
                    Column {
                        Text("Purchased", style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF1E3246)))
                        Text(
                            "${purchasedItems.size} items",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = PrimaryGreenDark)
                        )
                    }
                    Divider(
                        modifier = Modifier
                            .height(36.dp)
                            .width(1.dp),
                        color = BorderOutline
                    )
                    Column {
                        Text("Est. Cost", style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF1E3246)))
                        Text(
                            "$${String.format("%.2f", estimatedTotalCost)}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E3246))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filters Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Pending", "Purchased", "All").forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Shopping Items List
            if (displayedItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = SecondaryText
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No shopping items", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Your shopping list is clear!",
                            style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText)
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayedItems, key = { it.id }) { item ->
                        ShoppingItemRow(
                            item = item,
                            onTogglePurchased = {
                                val idx = shoppingList.indexOfFirst { s -> s.id == item.id }
                                if (idx != -1) {
                                    if (!item.isPurchased) {
                                        // Open purchase confirmation dialog
                                        purchaseDialogItem = item
                                    } else {
                                        // Revert to pending
                                        shoppingList[idx] = item.copy(isPurchased = false)
                                    }
                                }
                            },
                            onRemove = {
                                shoppingList.removeIf { s -> s.id == item.id }
                            }
                        )
                    }
                }
            }
        }
    }

    // Add Shopping Item Dialog
    if (showAddItemDialog) {
        var newItemName by remember { mutableStateOf("") }
        var newItemQty by remember { mutableStateOf("1.0") }
        var newItemUnit by remember { mutableStateOf("kg") }
        var newItemEstPrice by remember { mutableStateOf("5.00") }

        AlertDialog(
            onDismissRequest = { showAddItemDialog = false },
            title = { Text("Add Shopping Item") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newItemName,
                        onValueChange = { newItemName = it },
                        label = { Text("Item Name") },
                        singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newItemQty,
                            onValueChange = { newItemQty = it },
                            label = { Text("Qty") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newItemUnit,
                            onValueChange = { newItemUnit = it },
                            label = { Text("Unit") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    OutlinedTextField(
                        value = newItemEstPrice,
                        onValueChange = { newItemEstPrice = it },
                        label = { Text("Estimated Price ($)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newItemName.isNotBlank()) {
                            shoppingList.add(
                                ShoppingItemSample(
                                    name = newItemName.trim(),
                                    category = ProductCategory.OTHER_FOOD,
                                    requiredQuantity = newItemQty.toDoubleOrNull() ?: 1.0,
                                    unit = newItemUnit,
                                    estimatedPrice = newItemEstPrice.toDoubleOrNull() ?: 0.0,
                                    isPurchased = false
                                )
                            )
                            showAddItemDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) { Text("Add Item") }
            },
            dismissButton = {
                TextButton(onClick = { showAddItemDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Purchase Confirmation Dialog
    purchaseDialogItem?.let { pItem ->
        var actualPrice by remember { mutableStateOf(pItem.estimatedPrice.toString()) }
        var actualQty by remember { mutableStateOf(pItem.requiredQuantity.toString()) }

        AlertDialog(
            onDismissRequest = { purchaseDialogItem = null },
            title = { Text("Confirm Purchase of ${pItem.name}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("This will mark the item as purchased and update/create your local inventory stock batch.", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = actualQty,
                        onValueChange = { actualQty = it },
                        label = { Text("Actual Purchased Qty (${pItem.unit})") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = actualPrice,
                        onValueChange = { actualPrice = it },
                        label = { Text("Actual Total Cost ($)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val idx = shoppingList.indexOfFirst { s -> s.id == pItem.id }
                        if (idx != -1) {
                            shoppingList[idx] = pItem.copy(isPurchased = true)
                        }
                        purchaseDialogItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) { Text("Confirm & Update Inventory") }
            },
            dismissButton = {
                TextButton(onClick = { purchaseDialogItem = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun ShoppingItemRow(
    item: ShoppingItemSample,
    onTogglePurchased: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isPurchased) LightGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = item.isPurchased,
                    onCheckedChange = { onTogglePurchased() },
                    colors = CheckboxDefaults.colors(checkedColor = PrimaryGreen)
                )

                Column {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${item.requiredQuantity} ${item.unit} • Est: $${item.estimatedPrice}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText)
                    )
                    if (item.isAutoSuggested) {
                        Text(
                            text = "⚡ Low Stock Suggestion",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFC0392B), fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = SecondaryText
                )
            }
        }
    }
}
