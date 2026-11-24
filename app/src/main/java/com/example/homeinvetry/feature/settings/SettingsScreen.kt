package com.example.homeinvetry.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homeinvetry.core.notifications.NotificationHelper
import com.example.homeinvetry.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToAnalytics: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = { /* Settings Icon */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MainText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppBackground),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // My Household Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = LightGreen),
                    shape = RoundedCornerShape(20.dp),
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
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "My Household",
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "My Household",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )
                                )
                                Text(
                                    text = "Family Home",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText)
                                )
                            }
                        }

                        TextButton(onClick = { /* Manage */ }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Manage",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = PrimaryGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Settings Menu Items
            item {
                SettingsMenuRow(
                    title = "Household Analytics & Insights",
                    subtitle = "Monthly spending & category breakdown",
                    icon = Icons.Outlined.BarChart,
                    iconBgColor = LightGreen,
                    iconTint = PrimaryGreen,
                    onClick = onNavigateToAnalytics
                )
            }

            item {
                SettingsMenuRow(
                    title = "Notifications & Device Alerts",
                    subtitle = "Tap to trigger test push notification",
                    icon = Icons.Outlined.Notifications,
                    iconBgColor = LightYellow,
                    iconTint = Color(0xFFE67E22),
                    onClick = {
                        NotificationHelper.showNotification(
                            context,
                            1002,
                            "HomeStock Low Stock Alert 🚨",
                            "Cooking Oil (1 L) is low on stock! Tap to view shopping list."
                        )
                    }
                )
            }

            item {
                SettingsMenuRow(
                    title = "Backup & Synchronization",
                    subtitle = "Backup to Google Drive • Sync Devices",
                    icon = Icons.Outlined.CloudUpload,
                    iconBgColor = LightBlue,
                    iconTint = Color(0xFF2980B9)
                )
            }

            item {
                SettingsMenuRow(
                    title = "Household Members",
                    subtitle = "Manage Family Access",
                    icon = Icons.Outlined.Group,
                    iconBgColor = LightBlue,
                    iconTint = Color(0xFF2980B9)
                )
            }

            item {
                SettingsMenuRow(
                    title = "Appearance",
                    subtitle = "Light / Dark Mode",
                    icon = Icons.Outlined.Brightness4,
                    iconBgColor = Color(0xFFECEFF1),
                    iconTint = MainText
                )
            }

            item {
                SettingsMenuRow(
                    title = "Security & Privacy",
                    subtitle = "Lock App • Data Protection",
                    icon = Icons.Outlined.Security,
                    iconBgColor = LightBlue,
                    iconTint = Color(0xFF2980B9)
                )
            }

            item {
                SettingsMenuRow(
                    title = "About HomeStock",
                    subtitle = "Version 1.0.0",
                    icon = Icons.Outlined.Info,
                    iconBgColor = LightBlue,
                    iconTint = Color(0xFF2980B9)
                )
            }
        }
    }
}

@Composable
fun SettingsMenuRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SecondaryText,
                            fontSize = 12.sp
                        )
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
