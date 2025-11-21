package com.hornswoggled.ui.game

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hornswoggled.ui.components.*
import com.hornswoggled.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    gameId: String,
    onNavigateBack: () -> Unit
) {
    // Mock state - in production comes from ViewModel
    var roundWord by remember { mutableStateOf("HORNSWOGGLE") }
    var currentSubmission by remember { mutableStateOf("") }
    var timerProgress by remember { mutableStateOf(0.65f) }
    var currentRound by remember { mutableStateOf(2) }
    var totalRounds by remember { mutableStateOf(5) }
    var playerScore by remember { mutableStateOf(150) }
    val maxChars = 200

    // Animated word entrance
    var wordVisible by remember { mutableStateOf(false) }
    LaunchedEffect(roundWord) {
        wordVisible = false
        kotlinx.coroutines.delay(300)
        wordVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar with round info and score
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                MaterialTheme.colorScheme.surface,
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Round indicator
                    Surface(
                        shape = PillShape,
                        color = HornswoggledPurple
                    ) {
                        Text(
                            text = "🎯 Round $currentRound/$totalRounds",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    // Score
                    ScoreBadge(score = playerScore)
                }
            }

            // Main content area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Prompt
                Text(
                    text = "Define this word:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Word display with animation
                AnimatedVisibility(
                    visible = wordVisible,
                    enter = fadeIn(animationSpec = tween(600)) + scaleIn(
                        initialScale = 0.8f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 12.dp,
                                shape = GameCardShape,
                                ambientColor = HornswoggledPurple.copy(alpha = 0.3f),
                                spotColor = HornswoggledMagenta.copy(alpha = 0.3f)
                            ),
                        shape = GameCardShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            HornswoggledPurple.copy(alpha = 0.1f),
                                            HornswoggledMagenta.copy(alpha = 0.05f)
                                        )
                                    )
                                )
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = roundWord,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center,
                                letterSpacing = 2.sp,
                                style = MaterialTheme.typography.displayLarge.copy(
                                    shadow = Shadow(
                                        color = HornswoggledCyan.copy(alpha = 0.3f),
                                        blurRadius = 15f
                                    )
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Timer
                GameTimer(
                    progress = timerProgress,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Submission section
                GameCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✍️ Your Definition",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Character count
                            Surface(
                                shape = PillShape,
                                color = when {
                                    currentSubmission.length > maxChars -> ErrorRed
                                    currentSubmission.length > maxChars * 0.9 -> WarningAmber
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ) {
                                Text(
                                    text = "${currentSubmission.length}/$maxChars",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        currentSubmission.length > maxChars -> Color.White
                                        currentSubmission.length > maxChars * 0.9 -> Color.White
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = currentSubmission,
                            onValueChange = { if (it.length <= maxChars) currentSubmission = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    "Type your clever definition here...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            },
                            minLines = 4,
                            maxLines = 6,
                            shape = GameCardShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HornswoggledPurple,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tips
                        Surface(
                            shape = GameCardShape,
                            color = InfoBlue.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, InfoBlue.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "💡",
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Be creative! The funniest definition wins the round.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit button
                GameButton(
                    text = "🎯 SUBMIT ANSWER",
                    onClick = {
                        // Submit logic
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    enabled = currentSubmission.isNotBlank() && currentSubmission.length <= maxChars,
                    gradient = Brush.horizontalGradient(
                        colors = listOf(SuccessGreen, Color(0xFF00C853))
                    )
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating timer warning (when time is low)
        AnimatedVisibility(
            visible = timerProgress < 0.25f,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
        ) {
            Surface(
                shape = PillShape,
                color = ErrorRed,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⏰", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TIME RUNNING OUT!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
