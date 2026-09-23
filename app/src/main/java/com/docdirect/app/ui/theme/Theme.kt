package com.docdirect.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AuraPrimary,
    onPrimary = AuraOnPrimary,
    primaryContainer = AuraPrimaryContainer,
    onPrimaryContainer = AuraOnPrimaryContainer,
    secondary = AuraSecondary,
    onSecondary = Color.White,
    secondaryContainer = AuraSecondaryContainer,
    onSecondaryContainer = AuraOnSecondaryContainer,
    tertiary = AuraTertiary,
    onTertiary = Color.White,
    background = AuraBackground,
    surface = AuraSurface,
    surfaceVariant = AuraSurfaceContainerHighest,
    onBackground = AuraOnSurface,
    onSurface = AuraOnSurface,
    onSurfaceVariant = AuraOnSurfaceVariant,
    outline = AuraOutline,
    outlineVariant = AuraOutlineVariant,
    error = AuraError
)

private val DarkColorScheme = darkColorScheme(
    primary = AuraInversePrimary,
    onPrimary = AuraOnSurface,
    primaryContainer = AuraPrimary,
    onPrimaryContainer = AuraOnPrimaryContainer,
    secondary = AuraSecondaryFixedDim,
    onSecondary = AuraOnSurface,
    secondaryContainer = AuraSecondary,
    onSecondaryContainer = AuraSecondaryContainer,
    tertiary = Color(0xFF93CCFF),
    onTertiary = Color(0xFF001D31),
    background = AuraInverseSurface,
    surface = Color(0xFF1E2638),
    surfaceVariant = Color(0xFF333E54),
    onBackground = AuraInverseOnSurface,
    onSurface = AuraInverseOnSurface,
    onSurfaceVariant = Color(0xFFB0BDBC),
    outline = AuraOutlineVariant,
    outlineVariant = Color(0xFF4A5654),
    error = Color(0xFFFFB4AB)
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
