package com.example.homeinvetry.feature.reminders

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
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.homeinvetry.data.sample.*
import com.example.homeinvetry.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val remindersList = remember { SampleDataProvider.sampleReminders.toMutableStateList() }
    var selectedFilter by remember { mutableStateOf<ReminderType?>(null) } // null = All
    var showAddReminderDialog by remember { mutableStateOf(false) }

    val filteredReminders = if (selectedFilter == null) {
        remindersList
    } else {
        remindersList.filter { it.type == selectedFilter }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddReminderDialog = true },
                containerColor = PrimaryGreen,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Reminder")
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text("Reminders & Household Tasks", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Track product expiries, refills, maintenance & household chores",
                    style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText)
                )
            }

            // Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = { Text("All Reminders") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                items(ReminderType.entries.toTypedArray()) { type ->
                    val (label, icon) = when (type) {
                        ReminderType.EXPIRY -> "⏰ Expiry" to Icons.Default.Schedule
                        ReminderType.REFILL -> "🔄 Refill" to Icons.Default.Refresh
                        ReminderType.MAINTENANCE -> "🔧 Maintenance" to Icons.Default.Build
                        ReminderType.TASK -> "📋 Household Tasks" to Icons.Default.CheckCircleOutline
                    }
                    FilterChip(
                        selected = selectedFilter == type,
                        onClick = { selectedFilter = if (selectedFilter == type) null else type },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LightYellow,
                            selectedLabelColor = Color(0xFFB78103)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Reminders List
            if (filteredReminders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.Alarm,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = SecondaryText
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No reminders", style = MaterialTheme.typography.titleMedium)
                        Text("You're all caught up on tasks!", style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText))
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredReminders, key = { it.id }) { reminder ->
                        ReminderCardRow(
                            reminder = reminder,
                            onToggleComplete = {
                                val idx = remindersList.indexOfFirst { r -> r.id == reminder.id }
                                if (idx != -1) {
                                    remindersList[idx] = reminder.copy(isCompleted = !reminder.isCompleted)
                                }
                            },
                            onDelete = {
                                remindersList.removeIf { r -> r.id == reminder.id }
                            }
                        )
                    }
                }
            }
        }
    }

    // Add Custom Reminder Dialog
    if (showAddReminderDialog) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var selectedType by remember { mutableStateOf(ReminderType.TASK) }
        var dueDate by remember { mutableStateOf("Tomorrow") }
        var priority by remember { mutableStateOf("Medium") }

        AlertDialog(
            onDismissRequest = { showAddReminderDialog = false },
            title = { Text("Create New Household Task / Reminder") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Task Title *") },
                        placeholder = { Text("e.g. Service Air Conditioner") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description / Details") },
                        maxLines = 2
                    )
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("Due Date / Repeat") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            remindersList.add(
                                ReminderSample(
                                    title = title.trim(),
                                    description = description.trim(),
                                    type = selectedType,
                                    dueDate = dueDate.trim(),
                                    priority = priority
                                )
                            )
                            showAddReminderDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) { Text("Save Task") }
            },
            dismissButton = {
                TextButton(onClick = { showAddReminderDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun ReminderCardRow(
    reminder: ReminderSample,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    val (typeBg, typeColor, typeIcon) = when (reminder.type) {
        ReminderType.EXPIRY -> Triple(LightRed, Color(0xFFC0392B), Icons.Default.Schedule)
        ReminderType.REFILL -> Triple(LightYellow, Color(0xFFB78103), Icons.Default.Refresh)
        ReminderType.MAINTENANCE -> Triple(LightBlue, Color(0xFF1E3246), Icons.Default.Build)
        ReminderType.TASK -> Triple(LightGreen, PrimaryGreenDark, Icons.Default.CheckCircleOutline)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isCompleted) LightGreen.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
        ),
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onToggleComplete) {
                    Icon(
                        imageVector = if (reminder.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Complete",
                        tint = if (reminder.isCompleted) PrimaryGreen else SecondaryText
                    )
                }

                Column {
                    Text(
                        text = reminder.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = reminder.description,
                        style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(typeBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = reminder.type.name,
                                style = MaterialTheme.typography.labelSmall.copy(color = typeColor, fontWeight = FontWeight.Bold)
                            )
                        }
                        Text(
                            text = "Due: ${reminder.dueDate}",
                            style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText)
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = SecondaryText
                )
            }
        }
    }
}
