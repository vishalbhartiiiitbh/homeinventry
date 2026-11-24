package com.example.homeinvetry.feature.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.homeinvetry.core.ui.StatusBadge
import com.example.homeinvetry.data.sample.*
import com.example.homeinvetry.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    initialFilter: String? = null,
    onNavigateToAddItem: () -> Unit,
    onNavigateToItemDetail: (itemId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedLocationFilter by remember { mutableStateOf<StorageLocationType?>(
        initialFilter?.let {
            try { StorageLocationType.valueOf(it) } catch (e: Exception) { null }
        }
    ) }
    var selectedStatusFilter by remember { mutableStateOf<String?>(
        if (initialFilter == "LOW_STOCK") "LOW_STOCK" else if (initialFilter == "EXPIRING") "EXPIRING" else null
    ) }

    val allItems = remember { SampleDataProvider.sampleInventory.toMutableStateList() }

    val filteredItems = allItems.filter { item ->
        val matchesSearch = item.name.contains(searchQuery, ignoreCase = true) ||
                item.brand.contains(searchQuery, ignoreCase = true) ||
                item.category.displayName.contains(searchQuery, ignoreCase = true) ||
                item.notes.contains(searchQuery, ignoreCase = true)

        val matchesLocation = selectedLocationFilter == null || item.storageLocation == selectedLocationFilter
        val matchesStatus = when (selectedStatusFilter) {
            "LOW_STOCK" -> item.status == StockStatus.LOW_STOCK
            "EXPIRING" -> item.status == StockStatus.EXPIRING_SOON || item.status == StockStatus.EXPIRED
            else -> true
        }

        matchesSearch && matchesLocation && matchesStatus
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddItem,
                containerColor = PrimaryGreen,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Item")
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Household Inventory", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        text = "${filteredItems.size} items listed",
                        style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText)
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Search items, brands, categories...") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = BorderOutline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    FilterChip(
                        selected = selectedLocationFilter == null && selectedStatusFilter == null,
                        onClick = {
                            selectedLocationFilter = null
                            selectedStatusFilter = null
                        },
                        label = { Text("All Items") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedStatusFilter == "LOW_STOCK",
                        onClick = {
                            selectedStatusFilter = if (selectedStatusFilter == "LOW_STOCK") null else "LOW_STOCK"
                        },
                        label = { Text("⚠️ Low Stock") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LightRed,
                            selectedLabelColor = Color(0xFFC0392B)
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedStatusFilter == "EXPIRING",
                        onClick = {
                            selectedStatusFilter = if (selectedStatusFilter == "EXPIRING") null else "EXPIRING"
                        },
                        label = { Text("⏰ Expiring Soon") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LightYellow,
                            selectedLabelColor = Color(0xFFB78103)
                        )
                    )
                }
                items(StorageLocationType.entries.toTypedArray()) { loc ->
                    FilterChip(
                        selected = selectedLocationFilter == loc,
                        onClick = {
                            selectedLocationFilter = if (selectedLocationFilter == loc) null else loc
                        },
                        label = { Text(loc.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LightGreen,
                            selectedLabelColor = PrimaryGreenDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Inventory List
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = SecondaryText
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "No items found", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Try adjusting your search or filters.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText)
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        InventoryItemRow(
                            item = item,
                            onClick = { onNavigateToItemDetail(item.id) },
                            onConsumeClick = {
                                val index = allItems.indexOfFirst { it.id == item.id }
                                if (index != -1) {
                                    val current = allItems[index]
                                    val newQty = maxOf(0.0, current.quantity - 1.0)
                                    val newStatus = if (newQty <= current.minStock) StockStatus.LOW_STOCK else current.status
                                    allItems[index] = current.copy(quantity = newQty, status = newStatus)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryItemRow(
    item: InventoryItemSample,
    onClick: () -> Unit,
    onConsumeClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LightGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.name.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineMedium.copy(color = PrimaryGreenDark)
                    )
                }

                Column {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${item.brand} • ${item.category.displayName}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📍 ${item.storageLocation.displayName}",
                            style = MaterialTheme.typography.labelMedium.copy(color = SecondaryText)
                        )
                        StatusBadge(status = item.status)
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${item.quantity} ${item.unit}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = PrimaryGreen)
                )
                Text(
                    text = "Min: ${item.minStock} ${item.unit}",
                    style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText)
                )
                Spacer(modifier = Modifier.height(6.dp))
                IconButton(
                    onClick = onConsumeClick,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(LightRed)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Consume 1 Qty",
                        tint = Color(0xFFC0392B),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
