package com.example.homeinvetry.feature.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.homeinvetry.data.local.entity.InventoryItemEntity
import com.example.homeinvetry.data.sample.*
import com.example.homeinvetry.ui.theme.*
import com.example.homeinvetry.ui.viewmodel.AddItemViewModel
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    initialLocationName: String? = null,
    onNavigateBack: () -> Unit,
    onItemSaved: (InventoryItemEntity) -> Unit,
    modifier: Modifier = Modifier,
    addItemViewModel: AddItemViewModel = viewModel()
) {
    val context = LocalContext.current

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

    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    var expiryDate by remember { mutableStateOf(sdf.format(Date(System.currentTimeMillis() + 30L * 24 * 3600 * 1000))) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var notes by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }

    var expandedCategory by remember { mutableStateOf(false) }
    var expandedLocation by remember { mutableStateOf(false) }
    var expandedUnit by remember { mutableStateOf(false) }

    val unitsList = listOf("kg", "g", "litre", "ml", "pieces", "packets", "boxes", "bottles", "cans", "rolls", "sets")

    val barcodeScanner = remember(context) {
        val options = GmsBarcodeScannerOptions.Builder()
            .allowManualInput()
            .build()
        GmsBarcodeScanning.getClient(context, options)
    }

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
                label = { Text("Brand / Manufacturer") },
                placeholder = { Text("e.g. India Gate") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = !expandedCategory }
            ) {
                OutlinedTextField(
                    value = selectedCategory.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
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

            // Location Selector & Create Location
            Text("Storage Location", style = MaterialTheme.typography.titleMedium.copy(color = PrimaryGreen))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expandedLocation,
                    onExpandedChange = { expandedLocation = !expandedLocation },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedLocationName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Storage Location *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLocation) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedLocation,
                        onDismissRequest = { expandedLocation = false }
                    ) {
                        locationsList.forEach { locName ->
                            DropdownMenuItem(
                                text = { Text(locName) },
                                onClick = {
                                    selectedLocationName = locName
                                    expandedLocation = false
                                }
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { showCreateLocationDialog = true },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(48.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Location", tint = PrimaryGreen)
                }
            }

            OutlinedTextField(
                value = subLocation,
                onValueChange = { subLocation = it },
                label = { Text("Exact Sub-Location / Shelf / Box") },
                placeholder = { Text("e.g. Shelf 2, Box B") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Quantities & Alert Settings
            Text("Stock & Quantity Management", style = MaterialTheme.typography.titleMedium.copy(color = PrimaryGreen))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Current Qty *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
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

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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

            // Price, Barcode & Calendar Expiry Date Picker
            Text("Price & Dates", style = MaterialTheme.typography.titleMedium.copy(color = PrimaryGreen))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = purchasePrice,
                    onValueChange = { purchasePrice = it },
                    label = { Text("Purchase Price ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = { expiryDate = it },
                        label = { Text("Expiry Date *") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePickerDialog = true }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Pick Expiry Date from Calendar", tint = PrimaryGreen)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePickerDialog = true },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            OutlinedTextField(
                value = barcode,
                onValueChange = { barcode = it },
                label = { Text("Barcode") },
                placeholder = { Text("8901234567890") },
                trailingIcon = {
                    IconButton(onClick = {
                        try {
                            barcodeScanner.startScan()
                                .addOnSuccessListener { barcodeObj ->
                                    val raw = barcodeObj.rawValue
                                    if (!raw.isNullOrBlank()) {
                                        barcode = raw
                                    }
                                }
                        } catch (e: Exception) {
                            // Scanner fallback
                        }
                    }) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan Barcode", tint = PrimaryGreen)
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
                placeholder = { Text("Store in a cool dry place.") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }

    // Material 3 Calendar Date Picker Dialog
    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        expiryDate = sdf.format(Date(millis))
                    }
                    showDatePickerDialog = false
                }) {
                    Text("Select Date")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Create New Location Dialog
    if (showCreateLocationDialog) {
        AlertDialog(
            onDismissRequest = { showCreateLocationDialog = false },
            title = { Text("Create New Storage Location") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Enter a name for your new household location.", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = newLocationInput,
                        onValueChange = { newLocationInput = it },
                        label = { Text("Location Name *") },
                        placeholder = { Text("e.g. Wine Rack") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newLocationInput.isNotBlank()) {
                            val created = newLocationInput.trim()
                            addItemViewModel.createLocation(created)
                            locationsList.add(created)
                            selectedLocationName = created
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
}
