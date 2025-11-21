package com.hornswoggled.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.hornswoggled.ui.components.*
import com.hornswoggled.ui.theme.*
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    var displayName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Entrance animations
    var logoVisible by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        logoVisible = true
        kotlinx.coroutines.delay(300)
        contentVisible = true
    }

    // Animated gradient background
    val infiniteTransition = rememberInfiniteTransition(label = "background_gradient")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_animation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        HornswoggledPurple,
                        DeepPurple,
                        GameBackgroundDark
                    ),
                    center = Offset(
                        x = 500f + (300f * gradientOffset),
                        y = 500f + (300f * (1 - gradientOffset))
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 60.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated logo section
            AnimatedVisibility(
                visible = logoVisible,
                enter = fadeIn(animationSpec = tween(800)) + scaleIn(
                    initialScale = 0.8f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Pulsing icon
                    val iconScale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1500, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "icon_pulse"
                    )

                    Text(
                        text = "🎭",
                        fontSize = 80.sp,
                        modifier = Modifier.scale(iconScale)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "HORNSWOGGLED",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.displayLarge.copy(
                            shadow = Shadow(
                                color = HornswoggledCyan.copy(alpha = 0.5f),
                                blurRadius = 20f
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "The Ultimate Party Game",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = HornswoggledCyan,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Main content
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(600)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Welcome message
                    Text(
                        text = "Ready to outwit your friends?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Create hilarious fake definitions and trick other players into voting for yours!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Features preview
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FeatureItem(
                            icon = "🎯",
                            text = "Quick rounds perfect for parties"
                        )
                        FeatureItem(
                            icon = "😂",
                            text = "Hilarious gameplay with friends"
                        )
                        FeatureItem(
                            icon = "🏆",
                            text = "Score points for creativity and deception"
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Input section in card
                    GameCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "✨ Choose Your Name",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = displayName,
                            onValueChange = {
                                if (it.length <= 20) displayName = it
                            },
                            label = { Text("Display Name") },
                            placeholder = { Text("Enter your nickname") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading,
                            shape = GameCardShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HornswoggledPurple,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            supportingText = {
                                Text(
                                    "${displayName.length}/20",
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Info message
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
                                Text("🔒", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "No account needed - just pick a name and play!",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Error message
                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        Surface(
                            shape = GameCardShape,
                            color = ErrorRed.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⚠️", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = errorMessage ?: "",
                                    fontSize = 14.sp,
                                    color = ErrorRed,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Start button
                    GameButton(
                        text = if (isLoading) "JOINING..." else "🎮 START PLAYING",
                        onClick = {
                            isLoading = true
                            errorMessage = null

                            // Anonymous sign in with Firebase
                            FirebaseAuth.getInstance().signInAnonymously()
                                .addOnSuccessListener {
                                    onLoginSuccess()
                                }
                                .addOnFailureListener { e ->
                                    errorMessage = "Login failed: ${e.message}"
                                    isLoading = false
                                }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        enabled = displayName.isNotBlank() && !isLoading,
                        gradient = Brush.horizontalGradient(
                            colors = listOf(SuccessGreen, Color(0xFF00C853))
                        )
                    )

                    if (isLoading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        GameLoadingSpinner(modifier = Modifier.size(32.dp))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Footer
                    Text(
                        text = "By playing, you agree to have fun and\nlaugh at ridiculous definitions 😄",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureItem(
    icon: String,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = GameCardShape,
            color = Color.White.copy(alpha = 0.1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = icon,
                    fontSize = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
    }
}

// Placeholder ViewModel
class LoginViewModel : androidx.lifecycle.ViewModel() {
    // Add login logic here
}
