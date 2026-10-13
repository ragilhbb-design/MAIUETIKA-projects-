package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MaieutikaColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = ObsidianBlack,
    primaryContainer = GoldContainer,
    onPrimaryContainer = OnGoldContainer,
    secondary = GoldLight,
    onSecondary = ObsidianBlack,
    secondaryContainer = ObsidianCard,
    onSecondaryContainer = GoldLight,
    tertiary = MutedGold,
    onTertiary = ObsidianBlack,
    background = ObsidianBlack,
    onBackground = SoftWhite,
    surface = ObsidianSurface,
    onSurface = SoftWhite,
    surfaceVariant = ObsidianCard,
    onSurfaceVariant = SlateGray,
    outline = ObsidianBorder,
    outlineVariant = Color(0xFF222234),
    error = RiskHigh,
    onError = ObsidianBlack
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent luxury brand identity
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MaieutikaColorScheme,
        typography = Typography,
        content = content
    )
}
