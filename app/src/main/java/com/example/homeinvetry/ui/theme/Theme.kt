package com.example.homeinvetry.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryGreen,
    onPrimary = MainText,
    primaryContainer = DarkLightGreen,
    secondary = DarkPrimaryGreen,
    background = DarkBackground,
    surface = DarkSurfaceCard,
    onBackground = DarkMainText,
    onSurface = DarkMainText,
    onSurfaceVariant = DarkSecondaryText,
    outline = BorderOutline
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = SurfaceCard,
    primaryContainer = LightGreen,
    secondary = PrimaryGreenDark,
    background = AppBackground,
    surface = SurfaceCard,
    onBackground = MainText,
    onSurface = MainText,
    onSurfaceVariant = SecondaryText,
    outline = BorderOutline
)

@Composable
fun HomeStockTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our standard brand colors by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
