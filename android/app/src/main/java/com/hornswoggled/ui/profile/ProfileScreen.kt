package com.hornswoggled.ui.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.hornswoggled.ui.components.*
import com.hornswoggled.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    // Mock data - in production this would come from ViewModel
    val displayName = remember { mutableStateOf("Player") }
    val level = remember { mutableStateOf(5) }
    val xp = remember { mutableStateOf(750) }
    val coins = remember { mutableStateOf(1250) }
    val gamesPlayed = remember { mutableStateOf(42) }
    val gamesWon = remember { mutableStateOf(18) }
    val totalScore = remember { mutableStateOf(8450) }
    val winRate = remember {
        mutableStateOf(if (gamesPlayed.value > 0) (gamesWon.value * 100 / gamesPlayed.value) else 0)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Header with Gradient
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    HornswoggledPurple,
                                    HornswoggledMagenta
                                )
                            )
                        )
                        .padding(top = 48.dp, bottom = 32.dp)
                        .padding(horizontal = 20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Back and Settings buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = onNavigateBack,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }

                            IconButton(
                                onClick = onNavigateToSettings,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Avatar with pulsing ring
                        val infiniteTransition = rememberInfiniteTransition(label = "avatar_pulse")
                        val ringScale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.05f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1500, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "ring_scale"
                        )

                        Box(
                            modifier = Modifier.size(140.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Pulsing ring
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scale(ringScale)
                                    .border(4.dp, HornswoggledCyan, CircleShape)
                            )

                            // Avatar
                            PlayerAvatar(
                                name = displayName.value,
                                color = PlayerColor1,
                                size = 120.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Display Name
                        Text(
                            text = displayName.value,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Level Badge with gradient
                        Surface(
                            shape = PillShape,
                            color = HornswoggledYellow
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⭐", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Level ${level.value}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF2D1B00)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // XP Progress with styled card
                        GameCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "🎯 XP Progress",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "${xp.value} / 1000",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HornswoggledPurple
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                LinearProgressIndicator(
                                    progress = xp.value / 1000f,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(12.dp)
                                        .clip(PillShape),
                                    color = HornswoggledPurple,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Coins with pulsing animation
                        ScoreBadge(score = coins.value)
                    }
                }
            }

            // Statistics
            item {
                GameCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "📊 Statistics",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Stats grid
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                icon = "🎮",
                                label = "Played",
                                value = gamesPlayed.value.toString(),
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                icon = "🏆",
                                label = "Won",
                                value = gamesWon.value.toString(),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                icon = "📈",
                                label = "Win Rate",
                                value = "${winRate.value}%",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                icon = "⭐",
                                label = "Total Score",
                                value = totalScore.value.toString(),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Quick Actions
            item {
                GameCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "⚡ Quick Actions",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    GameButton(
                        text = "🖼️ View Gallery",
                        onClick = onNavigateToGallery,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        gradient = Brush.horizontalGradient(
                            colors = listOf(HornswoggledCyan, ElectricBlue)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GameOutlinedButton(
                        text = "✏️ Edit Profile",
                        onClick = { /* Edit Profile */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    )
                }
            }

            // Achievements Preview
            item {
                GameCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "🎖️ Recent Achievements",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    AchievementItem(
                        icon = "🏆",
                        title = "First Victory",
                        description = "Win your first game",
                        color = HornswoggledYellow
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AchievementItem(
                        icon = "❤️",
                        title = "Popular Choice",
                        description = "Receive 10 votes in one round",
                        color = HornswoggledMagenta
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AchievementItem(
                        icon = "🔥",
                        title = "On Fire",
                        description = "Win 3 games in a row",
                        color = HornswoggledOrange
                    )
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = GameCardShape,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AchievementItem(
    icon: String,
    title: String,
    description: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = GameCardShape,
            color = color.copy(alpha = 0.2f),
            border = BorderStroke(2.dp, color.copy(alpha = 0.5f)),
            modifier = Modifier.size(56.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = icon,
                    fontSize = 28.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Surface(
            shape = CircleShape,
            color = color
        ) {
            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
