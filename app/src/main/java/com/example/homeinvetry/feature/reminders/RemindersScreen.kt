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
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.homeinvetry.data.local.entity.ReminderEntity
import com.example.homeinvetry.data.sample.ReminderType
import com.example.homeinvetry.ui.theme.*
import com.example.homeinvetry.ui.viewmodel.RemindersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    remindersViewModel: RemindersViewModel = viewModel()
) {
    val remindersList by remindersViewModel.reminders.collectAsState()
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var showAddReminderDialog by remember { mutableStateOf(false) }

    val filteredReminders = if (selectedFilter == null) {
        remindersList
    } else {
        remindersList.filter { it.type.equals(selectedFilter, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reminders", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = { /* Notifications */ }) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Icon(Icons.Outlined.Notifications, contentDescription = "Reminders", tint = MainText, modifier = Modifier.size(26.dp))
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE74C3C))
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Outlined.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { showAddReminderDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Reminder")
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
            // Filter Pills Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilter == null,
                            onClick = { selectedFilter = null },
                            label = { Text("All") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGreen,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                    items(ReminderType.entries.toTypedArray()) { type ->
                        val label = when (type) {
                            ReminderType.EXPIRY -> "Expiry"
                            ReminderType.REFILL -> "Refill"
                            ReminderType.MAINTENANCE -> "Maintenance"
                            ReminderType.TASK -> "Tasks"
                        }
                        FilterChip(
                            selected = selectedFilter == type.name,
                            onClick = { selectedFilter = if (selectedFilter == type.name) null else type.name },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGreen,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }

            // Reminder Cards List from Room
            items(filteredReminders, key = { it.id }) { reminder ->
                ReminderCardViewEntity(
                    reminder = reminder,
                    onToggleComplete = { remindersViewModel.toggleComplete(reminder) }
                )
            }
        }
    }

    // Add Custom Reminder Dialog
    if (showAddReminderDialog) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var selectedType by remember { mutableStateOf("TASK") }
        var dueDate by remember { mutableStateOf("Tomorrow") }

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
                            remindersViewModel.addReminder(title.trim(), description.trim(), selectedType, dueDate.trim())
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
fun ReminderCardViewEntity(
    reminder: ReminderEntity,
    onToggleComplete: () -> Unit
) {
    val (cardBg, btnBg, btnTextTint) = when (reminder.type) {
        "EXPIRY" -> Triple(LightRed, Color(0xFFFADBD8), Color(0xFFC0392B))
        "REFILL" -> Triple(LightYellow, Color(0xFFFCF3CF), Color(0xFFB78103))
        "MAINTENANCE" -> Triple(LightBlue, Color(0xFFD6EAF8), Color(0xFF1E3246))
        else -> Triple(LightGreen, Color(0xFFD4EFDF), PrimaryGreenDark)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBg),
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
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Image / Icon Box
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = reminder.iconEmoji, fontSize = 24.sp)
                }

                Column {
                    Text(
                        text = reminder.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = reminder.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = SecondaryText,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            // Action Pill Button
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = btnBg,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { onToggleComplete() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (reminder.isCompleted) "Done ✔" else reminder.actionLabel,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = btnTextTint
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = btnTextTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
