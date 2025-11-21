package com.hornswoggled.ui.theme

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
    primary = HornswoggledPurple,
    onPrimary = Color.White,
    primaryContainer = DeepPurple,
    onPrimaryContainer = SoftLavender,

    secondary = HornswoggledMagenta,
    onSecondary = Color.White,
    secondaryContainer = HotPink,
    onSecondaryContainer = Color.White,

    tertiary = HornswoggledCyan,
    onTertiary = GameBackgroundDark,
    tertiaryContainer = ElectricBlue,
    onTertiaryContainer = Color.White,

    background = GameBackgroundDark,
    onBackground = Color.White,

    surface = GameSurfaceDark,
    onSurface = Color.White,
    surfaceVariant = GameCardDark,
    onSurfaceVariant = SoftLavender,

    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    outline = Color.White.copy(alpha = 0.2f),
    outlineVariant = Color.White.copy(alpha = 0.1f),
    scrim = Overlay,

    inverseSurface = GameSurfaceLight,
    inverseOnSurface = GameBackgroundDark,
    inversePrimary = HornswoggledPurple,

    surfaceTint = HornswoggledPurple,
)

private val LightColorScheme = lightColorScheme(
    primary = HornswoggledPurple,
    onPrimary = Color.White,
    primaryContainer = SoftLavender,
    onPrimaryContainer = DeepPurple,

    secondary = HornswoggledMagenta,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFD9E2),
    onSecondaryContainer = HotPink,

    tertiary = HornswoggledCyan,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB8F1FF),
    onTertiaryContainer = ElectricBlue,

    background = GameBackgroundLight,
    onBackground = Color(0xFF1C1B1F),

    surface = GameSurfaceLight,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = GameCardLight,
    onSurfaceVariant = Color(0xFF49454F),

    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    scrim = OverlayLight,

    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = SoftLavender,

    surfaceTint = HornswoggledPurple,
)

@Composable
fun HornswoggledTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Use dark background for status bar in dark mode, transparent in light
            window.statusBarColor = if (darkTheme) {
                colorScheme.background.toArgb()
            } else {
                Color.Transparent.toArgb()
            }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            // Enable edge-to-edge
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
