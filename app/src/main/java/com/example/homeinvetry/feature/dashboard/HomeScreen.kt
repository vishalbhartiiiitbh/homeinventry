package com.example.homeinvetry.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homeinvetry.core.ui.HomeStockHeader
import com.example.homeinvetry.core.ui.QuickActionButton
import com.example.homeinvetry.core.ui.StatusBadge
import com.example.homeinvetry.core.ui.SummaryCard
import com.example.homeinvetry.data.sample.*
import com.example.homeinvetry.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToInventory: (storageLocation: String?) -> Unit,
    onNavigateToShopping: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToAddItem: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToItemDetail: (itemId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = SampleDataProvider.sampleInventory
    val shoppingItems = SampleDataProvider.sampleShoppingList
    val reminders = SampleDataProvider.sampleReminders

    val totalItems = items.sumOf { it.quantity.toInt() }
    val lowStockCount = items.count { it.status == StockStatus.LOW_STOCK }
    val expiringSoonCount = items.count { it.status == StockStatus.EXPIRING_SOON || it.status == StockStatus.EXPIRED }
    val pendingShoppingCount = shoppingItems.count { !it.isPurchased }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        HomeStockHeader(
            onNotificationClick = onNavigateToReminders,
            onSearchClick = onNavigateToSearch
        )

        // Summary Cards Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryCard(
                title = "Total Items",
                value = "$totalItems",
                icon = Icons.Default.Category,
                containerColor = LightGreen,
                contentColor = PrimaryGreenDark,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToInventory(null) }
            )
            SummaryCard(
                title = "Low Stock",
                value = "$lowStockCount",
                icon = Icons.Default.WarningAmber,
                containerColor = LightRed,
                contentColor = Color(0xFFC0392B),
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToInventory("LOW_STOCK") }
            )
            SummaryCard(
                title = "Expiring Soon",
                value = "$expiringSoonCount",
                icon = Icons.Default.Schedule,
                containerColor = LightYellow,
                contentColor = Color(0xFFB78103),
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToInventory("EXPIRING") }
            )
            SummaryCard(
                title = "Shopping",
                value = "$pendingShoppingCount",
                icon = Icons.Default.ShoppingCart,
                containerColor = LightBlue,
                contentColor = Color(0xFF1E3246),
                modifier = Modifier.weight(1f),
                onClick = onNavigateToShopping
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Actions
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickActionButton(
                title = "Add Item",
                icon = Icons.Default.Add,
                color = LightGreen,
                onClick = onNavigateToAddItem,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                title = "Search",
                icon = Icons.Default.Search,
                color = LightYellow,
                onClick = onNavigateToSearch,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                title = "Shopping",
                icon = Icons.Default.ShoppingBag,
                color = LightBlue,
                onClick = onNavigateToShopping,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                title = "Add Task",
                icon = Icons.Default.AlarmAdd,
                color = LightRed,
                onClick = onNavigateToReminders,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Storage Locations Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Storage Locations",
                style = MaterialTheme.typography.titleLarge
            )
            TextButton(onClick = { onNavigateToInventory(null) }) {
                Text(text = "View All", color = PrimaryGreen)
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val locations = StorageLocationType.values().take(6)
            items(locations) { loc ->
                val locItems = items.filter { it.storageLocation == loc }
                val expiringInLoc = locItems.count { it.status == StockStatus.EXPIRING_SOON || it.status == StockStatus.EXPIRED }
                StorageLocationCard(
                    location = loc,
                    itemCount = locItems.size,
                    expiringCount = expiringInLoc,
                    onClick = { onNavigateToInventory(loc.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Low Stock Alerts
        if (lowStockCount > 0) {
            SectionHeader(
                title = "Low Stock Alerts",
                onViewAllClick = { onNavigateToInventory("LOW_STOCK") }
            )
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.filter { it.status == StockStatus.LOW_STOCK }.forEach { item ->
                    DashboardItemCard(item = item, onClick = { onNavigateToItemDetail(item.id) })
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Expiring Soon
        if (expiringSoonCount > 0) {
            SectionHeader(
                title = "Expiring & Opened Items",
                onViewAllClick = { onNavigateToInventory("EXPIRING") }
            )
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.filter { it.status == StockStatus.EXPIRING_SOON || it.status == StockStatus.EXPIRED }.forEach { item ->
                    DashboardItemCard(item = item, onClick = { onNavigateToItemDetail(item.id) })
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Upcoming Tasks & Reminders
        SectionHeader(
            title = "Upcoming Reminders",
            onViewAllClick = onNavigateToReminders
        )
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            reminders.take(3).forEach { reminder ->
                DashboardReminderCard(reminder = reminder, onClick = onNavigateToReminders)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun StorageLocationCard(
    location: StorageLocationType,
    itemCount: Int,
    expiringCount: Int,
    onClick: () -> Unit
) {
    val icon = when (location) {
        StorageLocationType.PANTRY -> Icons.Default.Kitchen
        StorageLocationType.KITCHEN -> Icons.Default.Countertops
        StorageLocationType.FRIDGE -> Icons.Default.AcUnit
        StorageLocationType.FREEZER -> Icons.Default.AcUnit
        StorageLocationType.BATHROOM -> Icons.Default.Bathtub
        StorageLocationType.STORE_ROOM -> Icons.Default.Inventory2
        StorageLocationType.CUSTOM -> Icons.Default.Place
    }

    Card(
        modifier = Modifier
            .width(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(LightGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = location.displayName,
                    tint = PrimaryGreenDark
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = location.displayName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$itemCount items",
                style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText)
            )
            if (expiringCount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⚠️ $expiringCount expiring",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFC0392B), fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, onViewAllClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        TextButton(onClick = onViewAllClick) {
            Text(text = "View All", color = PrimaryGreen)
        }
    }
}

@Composable
fun DashboardItemCard(item: InventoryItemSample, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
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
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(LightGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleLarge.copy(color = PrimaryGreenDark)
                    )
                }
                Column {
                    Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${item.storageLocation.displayName} • ${item.quantity} ${item.unit}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText)
                    )
                }
            }
            StatusBadge(status = item.status)
        }
    }
}

@Composable
fun DashboardReminderCard(reminder: ReminderSample, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
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
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val icon = when (reminder.type) {
                    ReminderType.EXPIRY -> Icons.Default.Schedule
                    ReminderType.REFILL -> Icons.Default.Refresh
                    ReminderType.MAINTENANCE -> Icons.Default.Build
                    ReminderType.TASK -> Icons.Default.CheckCircleOutline
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(LightBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF1E3246))
                }
                Column {
                    Text(text = reminder.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Due: ${reminder.dueDate}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText)
                    )
                }
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = SecondaryText)
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeStockTheme {
        HomeScreen(
            onNavigateToInventory = {},
            onNavigateToShopping = {},
            onNavigateToReminders = {},
            onNavigateToAddItem = {},
            onNavigateToSearch = {},
            onNavigateToItemDetail = {}
        )
    }
}

