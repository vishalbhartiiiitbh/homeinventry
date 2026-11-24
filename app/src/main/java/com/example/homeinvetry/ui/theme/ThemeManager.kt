package com.example.homeinvetry.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class ThemeMode(val displayName: String) {
    SYSTEM("System Default"),
    LIGHT("Light Theme"),
    DARK("Dark Theme")
}

object ThemeManager {
    var currentThemeMode by mutableStateOf(ThemeMode.SYSTEM)
}
