package com.example.homeinvetry.feature.settings

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.homeinvetry.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    var householdName by remember { mutableStateOf("My Sweet Home") }
    var expiryNotifs by remember { mutableStateOf(true) }
    var lowStockNotifs by remember { mutableStateOf(true) }
    var maintenanceNotifs by remember { mutableStateOf(true) }
    var darkThemeOption by remember { mutableStateOf("System Default") }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showSyncDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings & Household Config", style = MaterialTheme.typography.headlineMedium)

        // 1. My Household Card
        SettingsCard(title = "My Household", icon = Icons.Outlined.Home) {
            OutlinedTextField(
                value = householdName,
                onValueChange = { householdName = it },
                label = { Text("Household Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Household ID", style = MaterialTheme.typography.labelMedium.copy(color = SecondaryText))
                    Text("HH-9842-X7B", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
                TextButton(onClick = { /* Share HH ID */ }) {
                    Text("Share Code", color = PrimaryGreen)
                }
            }
        }

        // 2. Backup & Multi-device Sync Card
        SettingsCard(title = "Backup & Multi-Device Sync", icon = Icons.Outlined.CloudSync) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Last Google Drive Sync", style = MaterialTheme.typography.bodyMedium)
                    Text("Today at 09:45 AM • Synchronized", style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText))
                }
                Button(
                    onClick = { showSyncDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Sync Now")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Encrypted Backup (AES-256)", style = MaterialTheme.typography.bodyMedium)
                    Text("Last backup: Yesterday 10:00 PM", style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText))
                }
                OutlinedButton(
                    onClick = { showBackupDialog = true },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Backup Now")
                }
            }
        }

        // 3. Notifications Settings Card
        SettingsCard(title = "Notifications & Reminders", icon = Icons.Outlined.Notifications) {
            SwitchSettingRow(
                title = "Expiry Alerts",
                description = "Notify when food products near expiry date",
                checked = expiryNotifs,
                onCheckedChange = { expiryNotifs = it }
            )
            SwitchSettingRow(
                title = "Low Stock Alerts",
                description = "Notify when quantities fall below minimum threshold",
                checked = lowStockNotifs,
                onCheckedChange = { lowStockNotifs = it }
            )
            SwitchSettingRow(
                title = "Maintenance Reminders",
                description = "Notify for appliance checks & water filter cleaning",
                checked = maintenanceNotifs,
                onCheckedChange = { maintenanceNotifs = it }
            )
        }

        // 4. Appearance & Theme Card
        SettingsCard(title = "Appearance", icon = Icons.Outlined.Palette) {
            Text("App Theme", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Light", "Dark", "System Default").forEach { option ->
                    FilterChip(
                        selected = darkThemeOption == option,
                        onClick = { darkThemeOption = option },
                        label = { Text(option) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // 5. Data & Privacy Card
        SettingsCard(title = "Security & Privacy", icon = Icons.Outlined.Lock) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showClearDataDialog = true }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Clear Local Application Data", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFC0392B)))
                    Text("Deletes local Room database & cache with confirmation", style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText))
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFC0392B))
            }
        }

        // 6. About App Card
        SettingsCard(title = "About HomeStock", icon = Icons.Outlined.Info) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Application Version", style = MaterialTheme.typography.bodyMedium)
                Text("1.0.0 (Build 1)", style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText, fontWeight = FontWeight.Bold))
            }
            Text(
                "Offline-First Household Inventory & Multi-Device Sync Management.",
                style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Sync Dialog Preview
    if (showSyncDialog) {
        AlertDialog(
            onDismissRequest = { showSyncDialog = false },
            title = { Text("Multi-Device Synchronization") },
            text = {
                Text("Synchronizing local operation logs with shared household manifest on Google Drive...\n\nConflict Policy: First successfully committed change wins. Any conflicting updates are preserved in Conflict History.")
            },
            confirmButton = {
                Button(
                    onClick = { showSyncDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) { Text("Sync Complete") }
            }
        )
    }

    // Backup Dialog Preview
    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("Create Encrypted Backup") },
            text = {
                Text("Generating AES-256-GCM encrypted ZIP archive of database, images and setting metadata...\n\nFilename: HomeStock_Backup_20250301.zip.enc")
            },
            confirmButton = {
                Button(
                    onClick = { showBackupDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) { Text("Save Backup File") }
            }
        )
    }

    // Clear Data Dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("Clear All Local Data?") },
            text = {
                Text("Are you sure you want to reset all local inventory records, stock transactions, shopping items and reminders? This action cannot be undone unless you have a backup.")
            },
            confirmButton = {
                Button(
                    onClick = { showClearDataDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B))
                ) { Text("Clear Data") }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun SettingsCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LightGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = PrimaryGreenDark, modifier = Modifier.size(20.dp))
                }
                Text(text = title, style = MaterialTheme.typography.titleLarge)
            }
            HorizontalDivider()
            content()
        }
    }
}

@Composable
fun SwitchSettingRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = description, style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryGreen)
        )
    }
}
