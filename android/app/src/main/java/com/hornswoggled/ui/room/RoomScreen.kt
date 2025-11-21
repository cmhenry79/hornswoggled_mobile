package com.hornswoggled.ui.room

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.sp
import com.hornswoggled.ui.components.*
import com.hornswoggled.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomScreen(
    roomId: String,
    onNavigateToGame: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    // Mock state - in production comes from ViewModel
    var isHost by remember { mutableStateOf(true) }
    var roomName by remember { mutableStateOf("Epic Game Night") }
    var maxRounds by remember { mutableStateOf(5) }
    var timePerRound by remember { mutableStateOf(90) }

    val mockPlayers = remember {
        mutableStateListOf(
            PlayerData("1", "Alex", true, PlayerColor1, true),
            PlayerData("2", "Sam", true, PlayerColor2, false),
            PlayerData("3", "Jordan", false, PlayerColor3, false),
            PlayerData("4", "Riley", true, PlayerColor4, false)
        )
    }

    // Animation for player join
    var showPlayerJoinAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(mockPlayers.size) {
        showPlayerJoinAnimation = true
        kotlinx.coroutines.delay(500)
        showPlayerJoinAnimation = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Hero Header with gradient
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
                    // Back button and room name
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
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

                        Text(
                            text = roomName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        // Settings button (only for host)
                        if (isHost) {
                            IconButton(
                                onClick = { /* Show settings */ },
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
                        } else {
                            Spacer(modifier = Modifier.size(40.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Room code display with copy button
                    RoomCodeCard(roomCode = roomId.take(6).uppercase())
                }
            }

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Players section
                GameCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👥 Players",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Surface(
                            shape = PillShape,
                            color = HornswoggledPurple.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${mockPlayers.size}/8",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = HornswoggledPurple,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Player grid
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        mockPlayers.chunked(2).forEach { rowPlayers ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowPlayers.forEach { player ->
                                    PlayerCard(
                                        player = player,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                // Fill empty space if odd number
                                if (rowPlayers.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    // Waiting message if not enough players
                    if (mockPlayers.size < 2) {
                        Spacer(modifier = Modifier.height(16.dp))

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
                                Text("⏳", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Waiting for more players to join...",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Game settings preview
                GameCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚙️ Game Settings",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (isHost) {
                            TextButton(onClick = { /* Edit settings */ }) {
                                Text(
                                    "Edit",
                                    color = HornswoggledPurple,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Settings grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingItem(
                            icon = "🎯",
                            label = "Rounds",
                            value = maxRounds.toString(),
                            modifier = Modifier.weight(1f)
                        )

                        SettingItem(
                            icon = "⏱️",
                            label = "Time/Round",
                            value = "${timePerRound}s",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingItem(
                            icon = "📚",
                            label = "Word Pack",
                            value = "Classic",
                            modifier = Modifier.weight(1f)
                        )

                        SettingItem(
                            icon = "🤖",
                            label = "Bots",
                            value = "Off",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Ready status for non-host
                if (!isHost) {
                    var isReady by remember { mutableStateOf(false) }

                    GameButton(
                        text = if (isReady) "✓ READY" else "READY UP",
                        onClick = { isReady = !isReady },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        gradient = if (isReady)
                            Brush.horizontalGradient(colors = listOf(SuccessGreen, Color(0xFF00C853)))
                        else
                            Brush.horizontalGradient(colors = listOf(HornswoggledPurple, HornswoggledMagenta))
                    )
                } else {
                    // Start game button for host
                    val canStart = mockPlayers.size >= 2 && mockPlayers.count { it.isReady || it.isHost } >= mockPlayers.size

                    GameButton(
                        text = "🎮 START GAME",
                        onClick = { onNavigateToGame("game-123") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        enabled = canStart,
                        gradient = Brush.horizontalGradient(
                            colors = listOf(SuccessGreen, Color(0xFF00C853))
                        )
                    )

                    if (!canStart) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = PillShape,
                            color = WarningAmber.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, WarningAmber.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "⚠️ Need ${if (mockPlayers.size < 2) "at least 2 players" else "all players ready"}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = WarningAmber,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Player join animation
        AnimatedVisibility(
            visible = showPlayerJoinAnimation,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
        ) {
            Surface(
                shape = PillShape,
                color = SuccessGreen,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🎉", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "New player joined!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun RoomCodeCard(
    roomCode: String,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var showCopiedMessage by remember { mutableStateOf(false) }

    LaunchedEffect(showCopiedMessage) {
        if (showCopiedMessage) {
            kotlinx.coroutines.delay(2000)
            showCopiedMessage = false
        }
    }

    Surface(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = GameCardShape,
                ambientColor = Color.White.copy(alpha = 0.3f),
                spotColor = Color.White.copy(alpha = 0.3f)
            ),
        shape = GameCardShape,
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ROOM CODE",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = HornswoggledPurple.copy(alpha = 0.7f),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = roomCode,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp,
                color = HornswoggledPurple
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Copy button
            Surface(
                modifier = Modifier.clickable {
                    clipboardManager.setText(AnnotatedString(roomCode))
                    showCopiedMessage = true
                },
                shape = PillShape,
                color = if (showCopiedMessage) SuccessGreen else HornswoggledPurple
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showCopiedMessage) "✓" else "📋",
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (showCopiedMessage) "Copied!" else "Copy Code",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerCard(
    player: PlayerData,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = GameCardShape,
        color = player.color.copy(alpha = 0.1f),
        border = BorderStroke(2.dp, player.color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            PlayerAvatar(
                name = player.name,
                color = player.color,
                size = 44.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Player info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = player.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (player.isHost) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "👑",
                            fontSize = 14.sp
                        )
                    }
                }

                // Ready status
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (player.isReady || player.isHost) "✓ Ready" else "Waiting...",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (player.isReady || player.isHost) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SettingItem(
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
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Data classes
private data class PlayerData(
    val id: String,
    val name: String,
    val isReady: Boolean,
    val color: Color,
    val isHost: Boolean
)
