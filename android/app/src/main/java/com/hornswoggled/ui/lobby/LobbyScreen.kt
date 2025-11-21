package com.hornswoggled.ui.lobby

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hornswoggled.ui.components.*
import com.hornswoggled.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyScreen(
    onNavigateToRoom: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToStore: () -> Unit
) {
    var showCreateRoomDialog by remember { mutableStateOf(false) }
    var showJoinRoomDialog by remember { mutableStateOf(false) }

    // Mock data - in production comes from ViewModel
    val mockRooms = remember {
        listOf(
            RoomData("rm1", "Fun Times!", 4, 8, "playing"),
            RoomData("rm2", "Laugh Factory", 6, 8, "waiting"),
            RoomData("rm3", "Party Central", 2, 8, "waiting")
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Hero Header with Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PurpleMagentaVerticalGradient)
                    .padding(top = 48.dp, bottom = 24.dp)
                    .padding(horizontal = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🎭 HORNSWOGGLED",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Ready to play?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Store icon
                            IconButton(
                                onClick = onNavigateToStore,
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Text("🏪", fontSize = 24.sp)
                            }

                            // Profile icon
                            IconButton(
                                onClick = onNavigateToProfile,
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Text("👤", fontSize = 24.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Quick actions row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GameButton(
                            text = "🎮 CREATE ROOM",
                            onClick = { showCreateRoomDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp),
                            gradient = CyanBlueGradient
                        )

                        GameButton(
                            text = "🔗 JOIN",
                            onClick = { showJoinRoomDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp),
                            gradient = OrangeYellowGradient
                        )
                    }
                }
            }

            // Rooms list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "🌐 Public Rooms",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (mockRooms.isEmpty()) {
                    item {
                        EmptyRoomsCard()
                    }
                } else {
                    items(mockRooms) { room ->
                        RoomCard(
                            room = room,
                            onClick = { onNavigateToRoom(room.id) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    if (showCreateRoomDialog) {
        CreateRoomDialog(
            onDismiss = { showCreateRoomDialog = false },
            onCreate = { roomId ->
                showCreateRoomDialog = false
                onNavigateToRoom(roomId)
            }
        )
    }

    if (showJoinRoomDialog) {
        JoinRoomDialog(
            onDismiss = { showJoinRoomDialog = false },
            onJoin = { roomId ->
                showJoinRoomDialog = false
                onNavigateToRoom(roomId)
            }
        )
    }
}

@Composable
private fun RoomCard(
    room: RoomData,
    onClick: () -> Unit
) {
    GameCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = room.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Status badge
                    Surface(
                        shape = PillShape,
                        color = if (room.status == "waiting") SuccessGreen else WarningAmber
                    ) {
                        Text(
                            text = if (room.status == "waiting") "⏱ Waiting" else "▶️ Playing",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👥 ${room.currentPlayers}/${room.maxPlayers}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "🎯 ${(room.currentPlayers.toFloat() / room.maxPlayers * 100).toInt()}% full",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Join",
                tint = HornswoggledPurple,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Player count indicator
        LinearProgressIndicator(
            progress = room.currentPlayers.toFloat() / room.maxPlayers,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(PillShape),
            color = HornswoggledPurple,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun EmptyRoomsCard() {
    GameCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp)
        ) {
            Text(
                text = "🎭",
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No Active Rooms",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Be the first to create a room\nor join with a code!",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun CreateRoomDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var roomName by remember { mutableStateOf("") }
    var maxPlayers by remember { mutableStateOf(8) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                "🎮 Create Room",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = roomName,
                    onValueChange = { roomName = it },
                    label = { Text("Room Name") },
                    placeholder = { Text("Epic Game Night") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = GameCardShape,
                    singleLine = true
                )

                Column {
                    Text(
                        "Max Players: $maxPlayers",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Slider(
                        value = maxPlayers.toFloat(),
                        onValueChange = { maxPlayers = it.toInt() },
                        valueRange = 2f..12f,
                        steps = 9,
                        colors = SliderDefaults.colors(
                            thumbColor = HornswoggledPurple,
                            activeTrackColor = HornswoggledPurple
                        )
                    )
                }
            }
        },
        confirmButton = {
            GameButton(
                text = "Create",
                onClick = {
                    onCreate("room-${System.currentTimeMillis()}")
                },
                enabled = roomName.isNotBlank()
            )
        },
        dismissButton = {
            GameOutlinedButton(
                text = "Cancel",
                onClick = onDismiss
            )
        }
    )
}

@Composable
fun JoinRoomDialog(
    onDismiss: () -> Unit,
    onJoin: (String) -> Unit
) {
    var roomCode by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                "🔗 Join Room",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Enter the 6-digit room code:",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = roomCode,
                    onValueChange = { if (it.length <= 6) roomCode = it.uppercase() },
                    label = { Text("Room Code") },
                    placeholder = { Text("ABC123") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = GameCardShape,
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp,
                        textAlign = TextAlign.Center
                    )
                )

                if (roomCode.isNotEmpty() && roomCode.length < 6) {
                    Text(
                        "⚠️ Code must be 6 characters",
                        fontSize = 12.sp,
                        color = ErrorRed
                    )
                }
            }
        },
        confirmButton = {
            GameButton(
                text = "Join",
                onClick = { onJoin(roomCode) },
                enabled = roomCode.length == 6
            )
        },
        dismissButton = {
            GameOutlinedButton(
                text = "Cancel",
                onClick = onDismiss
            )
        }
    )
}

// Data class for room representation
private data class RoomData(
    val id: String,
    val name: String,
    val currentPlayers: Int,
    val maxPlayers: Int,
    val status: String // "waiting" or "playing"
)
