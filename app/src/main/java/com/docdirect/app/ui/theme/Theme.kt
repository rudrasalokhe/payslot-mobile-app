package com.docdirect.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = SurfaceLight,
    primaryContainer = TealLight,
    onPrimaryContainer = TealDark,
    secondary = MedicalBlue,
    onSecondary = SurfaceLight,
    secondaryContainer = MedicalBlueLight,
    onSecondaryContainer = MedicalNavy,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = TealAccent,
    onPrimary = MedicalNavy,
    primaryContainer = TealDark,
    onPrimaryContainer = TealLight,
    secondary = MedicalBlue,
    onSecondary = MedicalNavy,
    background = MedicalNavy,
    surface = Color(0xFF1E293B),
    onBackground = SurfaceLight,
    onSurface = SurfaceLight
)

@Composable
fun DocDirectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
