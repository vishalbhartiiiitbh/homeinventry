package com.example.homeinvetry.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.homeinvetry.ui.theme.PrimaryGreen
import com.example.homeinvetry.ui.theme.ThemeManager
import com.example.homeinvetry.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceDialog(
    onDismiss: () -> Unit
) {
    val currentMode = ThemeManager.currentThemeMode

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Brightness4, contentDescription = null, tint = PrimaryGreen)
                Text("Appearance Mode", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Choose your preferred theme mode:", style = MaterialTheme.typography.bodyMedium)

                ThemeMode.entries.forEach { mode ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                ThemeManager.currentThemeMode = mode
                                onDismiss()
                            },
                        color = if (currentMode == mode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = mode.displayName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = if (currentMode == mode) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                            RadioButton(
                                selected = (currentMode == mode),
                                onClick = {
                                    ThemeManager.currentThemeMode = mode
                                    onDismiss()
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryGreen)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
