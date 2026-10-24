package com.docdirect.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AvenTeal,
    onPrimary = AvenWhite,
    primaryContainer = AvenMint,
    onPrimaryContainer = AvenInk,
    secondary = AvenTeal,
    onSecondary = Color.White,
    secondaryContainer = AvenMint,
    onSecondaryContainer = AvenInk,
    tertiary = AvenBlue,
    onTertiary = Color.White,
    background = AvenBg,
    surface = AvenWhite,
    surfaceVariant = AvenSoft,
    surfaceContainer = AvenBg,
    surfaceContainerHigh = AvenWhite,
    surfaceContainerHighest = AvenSoft,
    onBackground = AvenInk,
    onSurface = AvenInk,
    onSurfaceVariant = AvenMuted,
    outline = AvenLine,
    outlineVariant = AvenLine,
    error = AvenRed
)

// Aven Design System maintains a consistent clinical light palette across all system themes
private val DarkColorScheme = LightColorScheme

@Composable
fun DocDirectTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
