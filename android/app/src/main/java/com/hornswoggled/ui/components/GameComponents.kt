package com.hornswoggled.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hornswoggled.ui.theme.*

/**
 * Primary game button with gradient background and press animation
 */
@Composable
fun GameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    gradient: Brush = Brush.horizontalGradient(
        colors = listOf(HornswoggledPurple, HornswoggledMagenta)
    )
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "button_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = GameButtonShape,
                ambientColor = HornswoggledPurple.copy(alpha = 0.5f),
                spotColor = HornswoggledMagenta.copy(alpha = 0.5f)
            )
            .clip(GameButtonShape)
            .background(if (enabled) gradient else Brush.horizontalGradient(
                listOf(Disabled, Disabled)
            ))
            .clickable(enabled = enabled) {
                isPressed = true
                onClick()
            }
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

/**
 * Secondary outlined button
 */
@Composable
fun GameOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "outlined_button_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .border(
                width = 2.dp,
                color = if (enabled) HornswoggledPurple else Disabled,
                shape = GameButtonShape
            )
            .clip(GameButtonShape)
            .clickable(enabled = enabled) {
                isPressed = true
                onClick()
            }
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) HornswoggledPurple else Disabled,
            textAlign = TextAlign.Center
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

/**
 * Vibrant game card with elevation and hover effect
 */
@Composable
fun GameCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevation: Dp = 8.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "card_scale"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .then(
                if (onClick != null) Modifier.clickable {
                    isPressed = true
                    onClick()
                } else Modifier
            ),
        shape = GameCardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

/**
 * Player avatar with color ring
 */
@Composable
fun PlayerAvatar(
    name: String,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .border(3.dp, color, CircleShape)
            .padding(3.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.take(2).uppercase(),
            fontSize = (size.value / 2.5).sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

/**
 * Animated countdown timer
 */
@Composable
fun GameTimer(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
        label = "timer_progress"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(PillShape),
            color = when {
                animatedProgress > 0.5f -> SuccessGreen
                animatedProgress > 0.25f -> WarningAmber
                else -> ErrorRed
            },
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp),
            color = when {
                animatedProgress > 0.5f -> SuccessGreen
                animatedProgress > 0.25f -> WarningAmber
                else -> ErrorRed
            }
        )
    }
}

/**
 * Score badge with animation
 */
@Composable
fun ScoreBadge(
    score: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "score_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "score_scale"
    )

    Surface(
        modifier = modifier.scale(scale),
        shape = PillShape,
        color = HornswoggledYellow,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⭐",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = score.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D1B00)
            )
        }
    }
}

/**
 * Room code display with copy functionality
 */
@Composable
fun RoomCodeDisplay(
    code: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = GameCardShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ROOM CODE",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = code,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * Animated loading spinner
 */
@Composable
fun GameLoadingSpinner(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spinner")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ),
        label = "spinner_rotation"
    )

    CircularProgressIndicator(
        modifier = modifier,
        color = HornswoggledPurple,
        strokeWidth = 4.dp
    )
}
