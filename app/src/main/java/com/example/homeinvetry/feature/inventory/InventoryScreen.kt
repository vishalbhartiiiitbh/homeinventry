package com.example.homeinvetry.feature.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.homeinvetry.data.local.entity.InventoryItemEntity
import com.example.homeinvetry.ui.theme.*
import com.example.homeinvetry.ui.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    initialFilter: String? = null,
    onNavigateToAddItem: (selectedLocationName: String) -> Unit,
    onNavigateToItemDetail: (itemId: String) -> Unit,
    modifier: Modifier = Modifier,
    inventoryViewModel: InventoryViewModel = viewModel()
) {
    val itemsList by inventoryViewModel.inventoryItems.collectAsState()
    val storageLocationsEntities by inventoryViewModel.storageLocations.collectAsState()
    val selectedLocationName by inventoryViewModel.selectedLocation.collectAsState()

    LaunchedEffect(initialFilter) {
        if (!initialFilter.isNullOrEmpty()) {
            inventoryViewModel.selectLocation(initialFilter)
        }
    }

    var showLocationSelectorDialog by remember { mutableStateOf(false) }
    var showCreateLocationDialog by remember { mutableStateOf(false) }
    var newLocationInput by remember { mutableStateOf("") }

    // Deletion states
    var itemToDelete by remember { mutableStateOf<InventoryItemEntity?>(null) }
    var locationToDelete by remember { mutableStateOf<String?>(null) }

    val displayedLocationTitle = if (selectedLocationName == "All") "All Inventories" else selectedLocationName
    val displayedItemCount = itemsList.size

    val selectedLocationIcon = when (selectedLocationName.lowercase()) {
        "pantry" -> Icons.Default.Kitchen
        "kitchen" -> Icons.Default.Countertops
        "fridge" -> Icons.Default.AcUnit
        "freezer" -> Icons.Default.AcUnit
        "bathroom" -> Icons.Default.Bathtub
        "store room" -> Icons.Default.Inventory2
        "all" -> Icons.Default.Category
        else -> Icons.Default.Place
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Outlined.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { showLocationSelectorDialog = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Switch Inventory Location")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddItem(selectedLocationName) },
                containerColor = PrimaryGreen,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item to $displayedLocationTitle", modifier = Modifier.size(28.dp))
            }
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppBackground),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Selected Location Banner Card (Interactive!)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { showLocationSelectorDialog = true },
                    colors = CardDefaults.cardColors(containerColor = LightYellow),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = selectedLocationIcon,
                                    contentDescription = displayedLocationTitle,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = displayedLocationTitle,
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 22.sp
                                        )
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Switch Location",
                                        tint = PrimaryGreenDark,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Text(
                                    text = "$displayedItemCount Items • Tap to switch or add new",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText, fontSize = 12.sp)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MainText,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Quick Stats Header & Pills
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Quick Stats",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        TextButton(onClick = { /* View Details */ }) {
                            Text(
                                text = "View Details →",
                                style = MaterialTheme.typography.labelMedium.copy(color = PrimaryGreen, fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    // 4 Stat Circles
                    val inStockCount = itemsList.count { it.status == "FRESH" }
                    val lowStockCount = itemsList.count { it.status == "LOW_STOCK" }
                    val expiringCount = itemsList.count { it.status == "EXPIRING_SOON" }
                    val expiredCount = itemsList.count { it.status == "EXPIRED" }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickStatCircle(count = "$inStockCount", label = "In Stock", color = PrimaryGreen, bgColor = LightGreen, modifier = Modifier.weight(1f))
                        QuickStatCircle(count = "$lowStockCount", label = "Low Stock", color = Color(0xFFE67E22), bgColor = LightYellow, modifier = Modifier.weight(1f))
                        QuickStatCircle(count = "$expiringCount", label = "Expiring", color = Color(0xFFE74C3C), bgColor = LightRed, modifier = Modifier.weight(1f))
                        QuickStatCircle(count = "$expiredCount", label = "Expired", color = SecondaryText, bgColor = Color(0xFFECEFF1), modifier = Modifier.weight(1f))
                    }
                }
            }

            // Items List Title
            item {
                Text(
                    text = "Items in $displayedLocationTitle",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            // Product Cards List from Room
            if (itemsList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Inbox,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = SecondaryText
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No items in $displayedLocationTitle", style = MaterialTheme.typography.titleMedium)
                            Text("Tap (+) to add items to this location.", style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText))
                        }
                    }
                }
            } else {
                items(itemsList, key = { it.id }) { item ->
                    PantryItemRowEntity(
                        item = item,
                        onClick = { onNavigateToItemDetail(item.id) },
                        onDeleteItem = { itemToDelete = item }
                    )
                }
            }
        }
    }

    // Storage Location Selector Dialog
    if (showLocationSelectorDialog) {
        AlertDialog(
            onDismissRequest = { showLocationSelectorDialog = false },
            title = {
                Text("Select Inventory Location", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Choose which household storage location to view or manage:",
                        style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // All Inventories Option
                    LocationOptionRow(
                        name = "All Inventories",
                        itemCount = itemsList.size,
                        isSelected = selectedLocationName == "All",
                        icon = Icons.Default.Category,
                        onClick = {
                            inventoryViewModel.selectLocation("All")
                            showLocationSelectorDialog = false
                        }
                    )

                    HorizontalDivider()

                    // Storage Locations List from Room
                    storageLocationsEntities.forEach { locEntity ->
                        val locName = locEntity.name
                        val locIcon = when (locName.lowercase()) {
                            "pantry" -> Icons.Default.Kitchen
                            "kitchen" -> Icons.Default.Countertops
                            "fridge" -> Icons.Default.AcUnit
                            "freezer" -> Icons.Default.AcUnit
                            "bathroom" -> Icons.Default.Bathtub
                            "store room" -> Icons.Default.Inventory2
                            else -> Icons.Default.Place
                        }

                        LocationOptionRow(
                            name = locName,
                            itemCount = 0,
                            isSelected = selectedLocationName.equals(locName, ignoreCase = true),
                            icon = locIcon,
                            onClick = {
                                inventoryViewModel.selectLocation(locName)
                                showLocationSelectorDialog = false
                            },
                            onDeleteLocation = {
                                locationToDelete = locName
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Create New Location Button inside Dialog
                    OutlinedButton(
                        onClick = {
                            showLocationSelectorDialog = false
                            showCreateLocationDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryGreen)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ Create New Storage Location", color = PrimaryGreen, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLocationSelectorDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Create New Location Dialog
    if (showCreateLocationDialog) {
        AlertDialog(
            onDismissRequest = { showCreateLocationDialog = false },
            title = { Text("Create New Storage Location") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Enter a name for your new household inventory location (e.g. Garage, Wine Cellar, Balcony).", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = newLocationInput,
                        onValueChange = { newLocationInput = it },
                        label = { Text("Location Name *") },
                        placeholder = { Text("e.g. Garage Shelf") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newLocationInput.isNotBlank()) {
                            val createdName = newLocationInput.trim()
                            inventoryViewModel.createLocation(createdName)
                            inventoryViewModel.selectLocation(createdName)
                            newLocationInput = ""
                            showCreateLocationDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("Create Location")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateLocationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Item Confirmation Dialog
    itemToDelete?.let { targetItem ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete '${targetItem.name}'?") },
            text = {
                Text(
                    "Are you sure you want to delete this inventory item? All associated stock batches and records will be permanently removed from Room database.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        inventoryViewModel.deleteItem(targetItem)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B))
                ) {
                    Text("Delete Item")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Storage Location Confirmation Dialog
    locationToDelete?.let { locName ->
        AlertDialog(
            onDismissRequest = { locationToDelete = null },
            title = { Text("Delete Location '$locName'?") },
            text = {
                Text(
                    "Are you sure you want to delete this storage location from Room database?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        inventoryViewModel.deleteLocation(locName)
                        if (selectedLocationName.equals(locName, ignoreCase = true)) {
                            inventoryViewModel.selectLocation("All")
                        }
                        locationToDelete = null
                        showLocationSelectorDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B))
                ) {
                    Text("Delete Location")
                }
            },
            dismissButton = {
                TextButton(onClick = { locationToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun LocationOptionRow(
    name: String,
    itemCount: Int,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    onDeleteLocation: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = if (isSelected) LightGreen else MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) PrimaryGreenDark else SecondaryText,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) PrimaryGreenDark else MainText
                    )
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (onDeleteLocation != null && name != "All Inventories") {
                    IconButton(onClick = onDeleteLocation, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete Location",
                            tint = Color(0xFFC0392B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickStatCircle(
    count: String,
    label: String,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText, fontSize = 11.sp),
            maxLines = 1
        )
    }
}

@Composable
fun PantryItemRowEntity(
    item: InventoryItemEntity,
    onClick: () -> Unit,
    onDeleteItem: () -> Unit
) {
    var expandedMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Product Emoji / Icon Box
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(LightYellow),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.iconEmoji, fontSize = 24.sp)
                }

                Column {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "✔ ${item.quantity.toInt()} ${item.unit} • ${item.batchCount} Batch${if (item.batchCount > 1) "es" else ""}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "📅 Exp: ${item.expiryDate}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFE74C3C),
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Quantity Dropdown Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = LightGreen,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${item.quantity.toInt()} ${item.unit}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreenDark
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Dropdown",
                            tint = PrimaryGreenDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // 3-Dots Action Menu Button
                Box {
                    IconButton(onClick = { expandedMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            tint = SecondaryText,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Text("View / Edit Details")
                                }
                            },
                            onClick = {
                                expandedMenu = false
                                onClick()
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Outlined.Delete, contentDescription = null, tint = Color(0xFFC0392B), modifier = Modifier.size(18.dp))
                                    Text("Delete Item", color = Color(0xFFC0392B), fontWeight = FontWeight.Bold)
                                }
                            },
                            onClick = {
                                expandedMenu = false
                                onDeleteItem()
                            }
                        )
                    }
                }
            }
        }
    }
}
