package com.dualverse.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ============================================
// DualVerse Beta - Material You Theme
// A fresh, modern gaming aesthetic
// ============================================

// Primary Gradient Colors
val DualVersePurple = Color(0xFF7C3AED)
val DualVerseBlue = Color(0xFF3B82F6)
val DualVerseCyan = Color(0xFF06B6D4)

// Dark Theme Colors
val DarkBackground = Color(0xFF0A0A0F)
val DarkSurface = Color(0xFF13131A)
val DarkSurfaceVariant = Color(0xFF1E1E28)
val DarkCard = Color(0xFF1A1A24)

// Accent Colors
val NeonPurple = Color(0xFFA855F7)
val NeonBlue = Color(0xFF60A5FA)
val NeonGreen = Color(0xFF34D399)
val NeonPink = Color(0xFFF472B6)
val NeonOrange = Color(0xFFFB923C)

// Status Colors
val SuccessGreen = Color(0xFF22C55E)
val WarningYellow = Color(0xFFEAB308)
val ErrorRed = Color(0xFFEF4444)

private val DarkColorScheme = darkColorScheme(
    primary = DualVersePurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4C1D95),
    onPrimaryContainer = Color(0xFFEDE9FE),
    secondary = DualVerseBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E3A5F),
    onSecondaryContainer = Color(0xFFDBEAFE),
    tertiary = DualVerseCyan,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFF9CA3AF),
    outline = Color(0xFF374151),
    outlineVariant = Color(0xFF1F2937),
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun DualVerseBetaTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = DarkBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Gradient Brushes for UI elements
object DualVerseGradients {
    val primaryGradient = Brush.linearGradient(
        colors = listOf(DualVersePurple, DualVerseBlue)
    )

    val accentGradient = Brush.linearGradient(
        colors = listOf(NeonPurple, NeonPink)
    )

    val successGradient = Brush.linearGradient(
        colors = listOf(NeonGreen, DualVerseCyan)
    )

    val cardGradient = Brush.linearGradient(
        colors = listOf(DarkSurface, DarkSurfaceVariant)
    )

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(DarkBackground, Color(0xFF0F0F18))
    )
}
