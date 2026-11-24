package com.example.homeinvetry.feature.sync

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homeinvetry.data.local.HomeStockDatabase
import com.example.homeinvetry.data.local.entity.SyncConflictEntity
import com.example.homeinvetry.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncConflictDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val db = remember(context) { HomeStockDatabase.getDatabase(context) }
    val conflictsList by db.syncDao().getAllConflictsFlow().collectAsState(initial = emptyList())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.MergeType, contentDescription = null, tint = PrimaryGreen)
                Text("Sync Conflict History", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = LightYellow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "When concurrent changes occur across multiple devices, HomeStock applies First-Committed-Wins. All losing conflicting operations are preserved below for your review.",
                        style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText, fontSize = 11.sp),
                        modifier = Modifier.padding(10.dp)
                    )
                }

                if (conflictsList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No conflicting operations detected. All device logs are in sync!", style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText))
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(conflictsList, key = { it.conflictId }) { conflict ->
                            ConflictItemCard(conflict = conflict)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun ConflictItemCard(conflict: SyncConflictEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${conflict.entityType} (${conflict.entityId.take(8)})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = if (conflict.isResolved) "Resolved" else "Preserved",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (conflict.isResolved) PrimaryGreen else PrimaryGreenDark,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Text("Reason: ${conflict.conflictReason}", style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText))
            Text("Losing Device: ${conflict.losingDeviceId.take(12)}...", style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText))
            if (conflict.losingPayloadJson.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AppBackground,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = conflict.losingPayloadJson,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }
    }
}
