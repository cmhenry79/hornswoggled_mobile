package com.hornswoggled.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Common gradient brushes used throughout the app for consistent visual design
 */

// Primary brand gradients
val PurpleMagentaGradient = Brush.horizontalGradient(
    colors = listOf(HornswoggledPurple, HornswoggledMagenta)
)

val PurpleMagentaVerticalGradient = Brush.verticalGradient(
    colors = listOf(HornswoggledPurple, HornswoggledMagenta)
)

// Accent gradients
val CyanBlueGradient = Brush.horizontalGradient(
    colors = listOf(HornswoggledCyan, ElectricBlue)
)

val OrangeYellowGradient = Brush.horizontalGradient(
    colors = listOf(HornswoggledOrange, HornswoggledYellow)
)

// Success gradient (for primary CTAs)
val GreenSuccessGradient = Brush.horizontalGradient(
    colors = listOf(SuccessGreen, Color(0xFF00C853))
)

// Disabled gradient
val DisabledGradient = Brush.horizontalGradient(
    colors = listOf(Disabled, Disabled)
)

// Radial gradients for backgrounds
fun createRadialBackgroundGradient(centerOffsetX: Float, centerOffsetY: Float) = Brush.radialGradient(
    colors = listOf(HornswoggledPurple, DeepPurple, GameBackgroundDark),
    center = androidx.compose.ui.geometry.Offset(centerOffsetX, centerOffsetY)
)

// Card gradients
val PurpleCardGradient = Brush.radialGradient(
    colors = listOf(
        HornswoggledPurple.copy(alpha = 0.1f),
        HornswoggledMagenta.copy(alpha = 0.05f)
    )
)
