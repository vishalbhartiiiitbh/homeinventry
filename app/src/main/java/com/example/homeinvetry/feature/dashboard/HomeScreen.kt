package com.example.homeinvetry.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.homeinvetry.core.ui.HomeStockHeader
import com.example.homeinvetry.core.ui.MetricCard
import com.example.homeinvetry.core.ui.QuickActionButton
import com.example.homeinvetry.data.local.entity.InventoryItemEntity
import com.example.homeinvetry.data.local.entity.ReminderEntity
import com.example.homeinvetry.ui.theme.*
import com.example.homeinvetry.ui.viewmodel.DashboardViewModel

@Composable
fun HomeScreen(
    onNavigateToInventory: (storageLocation: String?) -> Unit,
    onNavigateToShopping: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToAddItem: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToItemDetail: (itemId: String) -> Unit,
    modifier: Modifier = Modifier,
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    val totalItems by dashboardViewModel.totalItemsCount.collectAsState()
    val lowStockCount by dashboardViewModel.lowStockCount.collectAsState()
    val expiringCount by dashboardViewModel.expiringCount.collectAsState()
    val pendingShoppingCount by dashboardViewModel.pendingShoppingCount.collectAsState()

    val storageLocations by dashboardViewModel.storageLocations.collectAsState()
    val lowStockItems by dashboardViewModel.lowStockItems.collectAsState()
    val expiringItems by dashboardViewModel.expiringItems.collectAsState()
    val upcomingReminders by dashboardViewModel.upcomingReminders.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Logo Header
        HomeStockHeader(
            onNotificationClick = onNavigateToReminders,
            onSearchClick = onNavigateToSearch
        )

        // Greeting Header
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Text(
                text = "Good Morning!",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
            )
            Text(
                text = "Your household at a glance 👋",
                style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2x2 Metric Summary Grid from Room DB
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard(
                    value = "$totalItems",
                    title = "Total Items",
                    icon = Icons.Default.Category,
                    containerColor = LightGreen,
                    iconBgColor = Color.White,
                    iconTint = PrimaryGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToInventory(null) }
                )
                MetricCard(
                    value = "$lowStockCount",
                    title = "Low Stock",
                    icon = Icons.Default.WarningAmber,
                    containerColor = LightYellow,
                    iconBgColor = Color.White,
                    iconTint = Color(0xFFE67E22),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToInventory("LOW_STOCK") }
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard(
                    value = "$expiringCount",
                    title = "Expiring Soon",
                    icon = Icons.Default.MedicalServices,
                    containerColor = LightRed,
                    iconBgColor = Color.White,
                    iconTint = Color(0xFFE74C3C),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToInventory("EXPIRING") }
                )
                MetricCard(
                    value = "$pendingShoppingCount",
                    title = "Shopping Items",
                    icon = Icons.Default.ShoppingCart,
                    containerColor = LightBlue,
                    iconBgColor = Color.White,
                    iconTint = PrimaryGreen,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToShopping
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Storage Locations Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Storage Locations",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            TextButton(onClick = { onNavigateToInventory(null) }) {
                Text(
                    text = "View All →",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Storage Locations 2x2 Grid from Room DB
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val displayLocations = if (storageLocations.isNotEmpty()) storageLocations.take(4) else emptyList()
            if (displayLocations.size >= 2) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val loc1 = displayLocations[0]
                    val loc2 = displayLocations[1]
                    StorageLocationGridCard(
                        title = loc1.name,
                        itemCountText = "Location",
                        icon = Icons.Default.Kitchen,
                        bgColor = LightYellow,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToInventory(loc1.name) }
                    )
                    StorageLocationGridCard(
                        title = loc2.name,
                        itemCountText = "Location",
                        icon = Icons.Default.Countertops,
                        bgColor = LightGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToInventory(loc2.name) }
                    )
                }
            }
            if (displayLocations.size >= 4) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val loc3 = displayLocations[2]
                    val loc4 = displayLocations[3]
                    StorageLocationGridCard(
                        title = loc3.name,
                        itemCountText = "Location",
                        icon = Icons.Default.AcUnit,
                        bgColor = LightBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToInventory(loc3.name) }
                    )
                    StorageLocationGridCard(
                        title = loc4.name,
                        itemCountText = "Location",
                        icon = Icons.Default.AcUnit,
                        bgColor = Color(0xFFE0F7FA),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToInventory(loc4.name) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Actions Section
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickActionButton(
                title = "Add Item",
                icon = Icons.Default.Add,
                containerColor = LightGreen,
                iconTint = PrimaryGreen,
                onClick = onNavigateToAddItem,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                title = "Search",
                icon = Icons.Outlined.Search,
                containerColor = LightGreen,
                iconTint = PrimaryGreen,
                onClick = onNavigateToSearch,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                title = "Shopping List",
                icon = Icons.Default.ShoppingCart,
                containerColor = LightGreen,
                iconTint = PrimaryGreen,
                onClick = onNavigateToShopping,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                title = "Add Reminder",
                icon = Icons.Default.Event,
                containerColor = LightGreen,
                iconTint = PrimaryGreen,
                onClick = onNavigateToReminders,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun StorageLocationGridCard(
    title: String,
    itemCountText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MainText,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = itemCountText,
                        style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText, fontSize = 12.sp)
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = SecondaryText,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
