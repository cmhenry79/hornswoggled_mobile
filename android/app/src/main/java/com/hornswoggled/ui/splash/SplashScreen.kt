package com.hornswoggled.ui.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.hornswoggled.ui.components.GameLoadingSpinner
import com.hornswoggled.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToLobby: () -> Unit
) {
    var logoVisible by remember { mutableStateOf(false) }
    var taglineVisible by remember { mutableStateOf(false) }
    var loadingVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Stagger animations
        delay(200)
        logoVisible = true
        delay(400)
        taglineVisible = true
        delay(400)
        loadingVisible = true

        // Wait total 2.5 seconds
        delay(1500)

        // Check authentication and navigate
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            onNavigateToLobby()
        } else {
            onNavigateToLogin()
        }
    }

    // Animated gradient background
    val infiniteTransition = rememberInfiniteTransition(label = "background_gradient")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
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
                    center = androidx.compose.ui.geometry.Offset(
                        x = 500f * gradientOffset,
                        y = 500f * (1 - gradientOffset)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Animated logo/icon
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
                        targetValue = 1.15f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1500, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "icon_pulse"
                    )

                    Text(
                        text = "🎭",
                        fontSize = 100.sp,
                        modifier = Modifier.scale(iconScale)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // App title with glow effect
                    Text(
                        text = "HORNSWOGGLED",
                        fontSize = 52.sp,
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
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated tagline
            AnimatedVisibility(
                visible = taglineVisible,
                enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(800)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "The Ultimate Party Game",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = HornswoggledCyan,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Outfox. Outwit. Hornswoggle.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Loading indicator
            AnimatedVisibility(
                visible = loadingVisible,
                enter = fadeIn(animationSpec = tween(600))
            ) {
                GameLoadingSpinner(
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        // Version info at bottom
        AnimatedVisibility(
            visible = loadingVisible,
            enter = fadeIn(animationSpec = tween(800)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "v1.0.0",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Text(
                    text = "Made with ❤️ for party lovers",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
