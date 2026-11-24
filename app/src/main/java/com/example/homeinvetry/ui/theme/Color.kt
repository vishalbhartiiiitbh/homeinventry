package com.example.homeinvetry.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryGreen = Color(0xFF168A45)
val PrimaryGreenDark = Color(0xFF116D36)
val LightGreen = Color(0xFFE7F4D8)
val LightYellow = Color(0xFFFFF2D2)
val LightBlue = Color(0xFFDDEBF7)
val LightRed = Color(0xFFFCE3E5)
val AppBackground = Color(0xFFFAFBF8)
val SurfaceCard = Color(0xFFFFFFFF)
val MainText = Color(0xFF171A1C)
val SecondaryText = Color(0xFF73777B)
val BorderOutline = Color(0xFFE2E8F0)

// Dark theme colors
val DarkBackground = Color(0xFF121416)
val DarkSurfaceCard = Color(0xFF1E2226)
val DarkMainText = Color(0xFFE6E8EA)
val DarkSecondaryText = Color(0xFF9DA2A8)
val DarkPrimaryGreen = Color(0xFF2ECC71)
val DarkLightGreen = Color(0xFF1B382B)
val DarkLightYellow = Color(0xFF3D3520)
val DarkLightBlue = Color(0xFF1E3246)
val DarkLightRed = Color(0xFF422125)

@Composable
fun adaptiveLightGreen(isDark: Boolean = MaterialTheme.colorScheme.background == DarkBackground): Color {
    return if (isDark) DarkLightGreen else LightGreen
}

@Composable
fun adaptiveLightYellow(isDark: Boolean = MaterialTheme.colorScheme.background == DarkBackground): Color {
    return if (isDark) DarkLightYellow else LightYellow
}

@Composable
fun adaptiveLightBlue(isDark: Boolean = MaterialTheme.colorScheme.background == DarkBackground): Color {
    return if (isDark) DarkLightBlue else LightBlue
}

@Composable
fun adaptiveLightRed(isDark: Boolean = MaterialTheme.colorScheme.background == DarkBackground): Color {
    return if (isDark) DarkLightRed else LightRed
}
