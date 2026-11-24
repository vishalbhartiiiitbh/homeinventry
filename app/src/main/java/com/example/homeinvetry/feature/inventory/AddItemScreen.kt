package com.example.homeinvetry.feature.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.homeinvetry.data.local.entity.InventoryItemEntity
import com.example.homeinvetry.data.sample.*
import com.example.homeinvetry.ui.theme.*
import com.example.homeinvetry.ui.viewmodel.AddItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    initialLocationName: String? = null,
    onNavigateBack: () -> Unit,
    onItemSaved: (InventoryItemEntity) -> Unit,
    modifier: Modifier = Modifier,
    addItemViewModel: AddItemViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ProductCategory.RICE) }

    val storageLocationsEntities by addItemViewModel.storageLocations.collectAsState()
    val locationsList = remember(storageLocationsEntities) {
        if (storageLocationsEntities.isNotEmpty()) storageLocationsEntities.map { it.name }.toMutableStateList()
        else mutableStateListOf("Pantry", "Kitchen", "Fridge", "Freezer", "Bathroom", "Store Room")
    }

    var selectedLocationName by remember { mutableStateOf(initialLocationName ?: "Pantry") }
    var showCreateLocationDialog by remember { mutableStateOf(false) }
    var newLocationInput by remember { mutableStateOf("") }

    var subLocation by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1.0") }
    var unit by remember { mutableStateOf("kg") }
    var minStock by remember { mutableStateOf("1.0") }
    var bufferStock by remember { mutableStateOf("2.0") }
    var purchasePrice by remember { mutableStateOf("0.00") }
    var expiryDate by remember { mutableStateOf("2026-03-01") }
    var notes by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }

    var expandedCategory by remember { mutableStateOf(false) }
    var expandedLocation by remember { mutableStateOf(false) }
    var expandedUnit by remember { mutableStateOf(false) }

    val unitsList = listOf("kg", "g", "litre", "ml", "pieces", "packets", "boxes", "bottles", "cans", "rolls", "sets")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Inventory Item", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                val qtyVal = quantity.toDoubleOrNull() ?: 1.0
                                val minVal = minStock.toDoubleOrNull() ?: 1.0
                                val bufVal = bufferStock.toDoubleOrNull() ?: (minVal * 1.5)
                                val statusVal = if (qtyVal <= minVal) "LOW_STOCK" else "FRESH"

                                val newItem = InventoryItemEntity(
                                    name = name.trim(),
                                    brand = brand.trim(),
                                    category = selectedCategory.displayName,
                                    storageLocation = selectedLocationName,
                                    subLocation = subLocation.trim(),
                                    quantity = qtyVal,
                                    minStock = minVal,
                                    bufferStock = bufVal,
                                    unit = unit,
                                    expiryDate = expiryDate,
                                    purchasePrice = purchasePrice.toDoubleOrNull() ?: 0.0,
                                    status = statusVal,
                                    notes = notes.trim(),
                                    barcode = barcode.trim()
                                )

                                addItemViewModel.saveItem(newItem)
                                onItemSaved(newItem)
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text("Save Item", color = Color.White)
                    }
                }
            }
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
            // Basic Details Section
            Text("Product Details", style = MaterialTheme.typography.titleMedium.copy(color = PrimaryGreen))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Item Name *") },
                placeholder = { Text("e.g. Basmati Rice") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text("Brand") },
                placeholder = { Text("e.g. India Gate") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Category & Storage Location Dropdowns
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Category
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedCategory.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        modifier = Modifier.menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        ProductCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.displayName) },
                                onClick = {
                                    selectedCategory = cat
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }

                // Storage Location
                ExposedDropdownMenuBox(
                    expanded = expandedLocation,
                    onExpandedChange = { expandedLocation = !expandedLocation },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedLocationName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Inventory") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLocation) },
                        modifier = Modifier.menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedLocation,
                        onDismissRequest = { expandedLocation = false }
                    ) {
                        locationsList.forEach { loc ->
                            DropdownMenuItem(
                                text = { Text(loc) },
                                onClick = {
                                    selectedLocationName = loc
                                    expandedLocation = false
                                }
                            )
                        }
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("+ New Location...", color = PrimaryGreen, fontWeight = FontWeight.Bold)
                                }
                            },
                            onClick = {
                                expandedLocation = false
                                showCreateLocationDialog = true
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = subLocation,
                onValueChange = { subLocation = it },
                label = { Text("Exact Sub-Location / Shelf / Box") },
                placeholder = { Text("e.g. Shelf 2 -> Box 4") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            HorizontalDivider()

            // Quantity & Stock Section
            Text("Stock & Quantity Management", style = MaterialTheme.typography.titleMedium.copy(color = PrimaryGreen))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Current Qty") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Unit Selector Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedUnit,
                    onExpandedChange = { expandedUnit = !expandedUnit },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = unit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnit) },
                        modifier = Modifier.menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedUnit,
                        onDismissRequest = { expandedUnit = false }
                    ) {
                        unitsList.forEach { u ->
                            DropdownMenuItem(
                                text = { Text(u) },
                                onClick = {
                                    unit = u
                                    expandedUnit = false
                                }
                            )
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = minStock,
                    onValueChange = { minStock = it },
                    label = { Text("Minimum Stock Alert") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = bufferStock,
                    onValueChange = { bufferStock = it },
                    label = { Text("Target Buffer Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            HorizontalDivider()

            // Pricing & Dates Section
            Text("Price & Dates", style = MaterialTheme.typography.titleMedium.copy(color = PrimaryGreen))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = purchasePrice,
                    onValueChange = { purchasePrice = it },
                    label = { Text("Purchase Price ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Expiry Date (YYYY-MM-DD)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            OutlinedTextField(
                value = barcode,
                onValueChange = { barcode = it },
                label = { Text("Barcode") },
                trailingIcon = {
                    IconButton(onClick = { /* Launch Barcode Camera Scanner */ }) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan Barcode")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes / Usage Instructions") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Create New Location Dialog
        if (showCreateLocationDialog) {
            AlertDialog(
                onDismissRequest = { showCreateLocationDialog = false },
                title = { Text("Create New Storage Location") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Type a name for the new inventory location:", style = MaterialTheme.typography.bodySmall)
                        OutlinedTextField(
                            value = newLocationInput,
                            onValueChange = { newLocationInput = it },
                            label = { Text("Location Name *") },
                            placeholder = { Text("e.g. Wine Cellar") },
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
                                addItemViewModel.createLocation(createdName)
                                selectedLocationName = createdName
                                newLocationInput = ""
                                showCreateLocationDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text("Create & Select")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateLocationDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
