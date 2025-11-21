package com.hornswoggled.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    // Small components: chips, buttons, small cards
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),

    // Medium components: cards, dialogs
    medium = RoundedCornerShape(16.dp),

    // Large components: bottom sheets, large cards
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// Custom shapes for game UI
val GameButtonShape = RoundedCornerShape(20.dp)
val GameCardShape = RoundedCornerShape(20.dp)
val PillShape = RoundedCornerShape(percent = 50)
val TopRoundedShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
val BottomRoundedShape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
