package com.example.homeinvetry.feature.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homeinvetry.core.sync.SyncEngine
import com.example.homeinvetry.feature.sync.SyncConflictDialog
import com.example.homeinvetry.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToAnalytics: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val syncEngine = remember(context) { SyncEngine(context) }

    var showAndroidBackupInfoDialog by remember { mutableStateOf(false) }
    var showSyncOptionsDialog by remember { mutableStateOf(false) }
    var showConflictDialog by remember { mutableStateOf(false) }
    var showAppearanceDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = { /* Settings Icon */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
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
                    title = "Multi-Device Sync & Conflicts",
                    subtitle = "First-Committed-Wins • Preserved conflict history",
                    icon = Icons.Outlined.Sync,
                    iconBgColor = LightBlue,
                    iconTint = Color(0xFF2980B9),
                    onClick = { showSyncOptionsDialog = true }
                )
            }

            item {
                SettingsMenuRow(
                    title = "Android Backup",
                    subtitle = "Managed by Android built-in backup service",
                    icon = Icons.Outlined.CloudDone,
                    iconBgColor = LightGreen,
                    iconTint = PrimaryGreen,
                    onClick = { showAndroidBackupInfoDialog = true }
                )
            }

            item {
                SettingsMenuRow(
                    title = "Appearance",
                    subtitle = "Light / Dark Mode • ${ThemeManager.currentThemeMode.displayName}",
                    icon = Icons.Outlined.Brightness4,
                    iconBgColor = Color(0xFFECEFF1),
                    iconTint = MaterialTheme.colorScheme.onBackground,
                    onClick = { showAppearanceDialog = true }
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

    // Appearance Theme Selector Dialog
    if (showAppearanceDialog) {
        AppearanceDialog(
            onDismiss = { showAppearanceDialog = false }
        )
    }

    // Multi-Device Sync Options Dialog
    if (showSyncOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showSyncOptionsDialog = false },
            title = { Text("Multi-Device Cloud Sync", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("HomeStock syncs per-device operation logs using First-Committed-Wins. All conflicting operations are preserved in history.", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))

                    Button(
                        onClick = {
                            showSyncOptionsDialog = false
                            scope.launch {
                                snackbarHostState.showSnackbar("Syncing device operation logs with cloud...")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.Sync, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sync Now")
                    }

                    OutlinedButton(
                        onClick = {
                            showSyncOptionsDialog = false
                            showConflictDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.MergeType, contentDescription = null, tint = PrimaryGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Sync Conflict History", color = PrimaryGreen, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSyncOptionsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Sync Conflict Dialog
    if (showConflictDialog) {
        SyncConflictDialog(
            onDismiss = { showConflictDialog = false }
        )
    }

    // Android Backup Info Dialog
    if (showAndroidBackupInfoDialog) {
        AlertDialog(
            onDismissRequest = { showAndroidBackupInfoDialog = false },
            title = { Text("Android Backup", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = LightGreen,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "HomeStock uses Android's built-in backup service to protect your household data. Backup and restore are managed through your Android device's backup settings.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = PrimaryGreenDark, fontWeight = FontWeight.Medium),
                            modifier = Modifier.padding(14.dp)
                        )
                    }

                    Text("Protected Household Data:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("• Inventory items, categories & storage locations", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        Text("• Stock batches, transactions & FEFO rules", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        Text("• Shopping lists & item preferences", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        Text("• Reminders, maintenance & household tasks", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedButton(
                        onClick = {
                            showAndroidBackupInfoDialog = false
                            try {
                                val intent = Intent("com.google.android.gms.backup.ACTION_BACKUP_SETTINGS")
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                try {
                                    val intent = Intent(Settings.ACTION_PRIVACY_SETTINGS)
                                    context.startActivity(intent)
                                } catch (ex: Exception) {
                                    // Fallback
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.Settings, contentDescription = null, tint = PrimaryGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Device Backup Settings", color = PrimaryGreen, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAndroidBackupInfoDialog = false }) {
                    Text("Close")
                }
            }
        )
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
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
