package com.docdirect.app.ui.theme

import androidx.compose.ui.graphics.Color

// Aven Design System Tokens
val AvenInk = Color(0xFF102F32)       // Primary text
val AvenTeal = Color(0xFF145B55)      // Primary actions / active state
val AvenDeep = Color(0xFF0B3937)      // Dark brand surfaces
val AvenLime = Color(0xFFDDFC91)      // Accent surfaces; pair with dark text
val AvenBg = Color(0xFFF5F7F3)        // Main canvas
val AvenWhite = Color(0xFFFFFFFF)     // Cards and fields
val AvenMuted = Color(0xFF5B6E70)     // Secondary text
val AvenLine = Color(0xFFDDE5DE)      // Borders and dividers
val AvenMint = Color(0xFFE8F1E9)      // Soft success/selected surfaces
val AvenSoft = Color(0xFFEEF2EE)      // Neutral disabled/surface fill
val AvenRed = Color(0xFFB43D39)       // Errors / destructive actions
val AvenRose = Color(0xFFFFF0ED)      // Soft error surface
val AvenAmber = Color(0xFF835E14)     // Warning text
val AvenSand = Color(0xFFFAF1D9)      // Warning surface
val AvenBlue = Color(0xFF456A95)      // Supporting blue
val AvenLavender = Color(0xFFEDEAF7)  // Supporting calendar surface

// Compatibility mappings for previous tokens so existing code keeps compiling
val AuraPrimary = AvenTeal
val AuraPrimaryContainer = AvenDeep
val AuraOnPrimary = AvenWhite
val AuraOnPrimaryContainer = AvenLime

val AuraSecondary = AvenTeal
val AuraSecondaryContainer = AvenMint
val AuraOnSecondaryContainer = AvenInk
val AuraSecondaryFixed = AvenLime
val AuraSecondaryFixedDim = AvenLime

val AuraTertiary = AvenBlue
val AuraTertiaryContainer = AvenLavender

val AuraBackground = AvenBg
val AuraSurface = AvenBg
val AuraSurfaceBright = AvenWhite
val AuraSurfaceContainerLowest = AvenWhite
val AuraSurfaceContainerLow = AvenSoft
val AuraSurfaceContainer = AvenMint
val AuraSurfaceContainerHigh = AvenLine
val AuraSurfaceContainerHighest = AvenSoft

val AuraOnSurface = AvenInk
val AuraOnSurfaceVariant = AvenMuted
val AuraOutline = AvenMuted
val AuraOutlineVariant = AvenLine
val AuraBorderSubtle = AvenLine

val AuraError = AvenRed
val AuraErrorContainer = AvenRose
val AuraOnError = AvenWhite

val AuraInverseSurface = AvenDeep
val AuraInverseOnSurface = AvenWhite
val AuraInversePrimary = AvenLime

val TealPrimary = AvenTeal
val TealDark = AvenDeep
val TealLight = AvenMint
val TealAccent = AvenLime
val MedicalNavy = AvenDeep
val MedicalBlue = AvenBlue
val MedicalBlueLight = AvenMint
val BackgroundLight = AvenBg
val SurfaceLight = AvenWhite
val TextPrimary = AvenInk
val TextSecondary = AvenMuted
val SuccessGreen = AvenTeal
val WarningOrange = AvenAmber
val ErrorRed = AvenRed
