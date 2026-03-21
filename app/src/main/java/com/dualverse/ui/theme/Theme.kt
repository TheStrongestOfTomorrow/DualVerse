package com.dualverse.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = Color.White,
    primaryContainer = PurpleGrey40,
    onPrimaryContainer = Color.White,
    secondary = PurpleGrey80,
    onSecondary = Color.White,
    secondaryContainer = DarkPurple,
    onSecondaryContainer = Color.White,
    tertiary = Pink80,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color.White,
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    onPrimary = Color.White,
    primaryContainer = Purple90,
    onPrimaryContainer = DarkPurple,
    secondary = PurpleGrey40,
    onSecondary = Color.White,
    secondaryContainer = PurpleGrey90,
    onSecondaryContainer = DarkPurple,
    tertiary = Pink40,
    background = LightBackground,
    onBackground = DarkText,
    surface = Color.White,
    onSurface = DarkText,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun DualVerseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Color definitions
val Purple80 = Color(0xFFB388FF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF7C4DFF)
val PurpleGrey40 = Color(0xFF625B71)
val Pink40 = Color(0xFF7D5260)

val Purple90 = Color(0xFFE8DDFF)
val PurpleGrey90 = Color(0xFFE8DEF8)

val DarkPurple = Color(0xFF4A148C)
val DarkBackground = Color(0xFF0D0D0D)
val DarkSurface = Color(0xFF1A1A1A)
val DarkSurfaceVariant = Color(0xFF2D2D2D)

val LightBackground = Color(0xFFFAFAFA)
val LightSurfaceVariant = Color(0xFFF5F5F5)

val DarkText = Color(0xFF1A1A1A)
val DarkTextSecondary = Color(0xFF666666)

val ErrorRed = Color(0xFFCF6679)

// Gradient colors for status cards
object GradientColors {
    val success = listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
    val warning = listOf(Color(0xFFFF9800), Color(0xFFF57C00))
    val error = listOf(Color(0xFFF44336), Color(0xFFD32F2F))
    val neutral = listOf(Color(0xFF607D8B), Color(0xFF455A64))
}
