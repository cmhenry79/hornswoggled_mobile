package com.hornswoggled.ui.lobby

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LobbyScreen(
    onNavigateToRoom: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToStore: () -> Unit
) {
    var showCreateRoomDialog by remember { mutableStateOf(false) }
    var showJoinRoomDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hornswoggled") },
                actions = {
                    IconButton(onClick = onNavigateToStore) {
                        Icon(Icons.Default.ShoppingCart, "Store")
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, "Profile")
                    }
                }
            )
        },
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = { showCreateRoomDialog = true },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Default.Add, "Create Room")
                }
                FloatingActionButton(
                    onClick = { showJoinRoomDialog = true }
                ) {
                    Icon(Icons.Default.Login, "Join Room")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Public Rooms",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Public rooms list would go here
            LazyColumn {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("No active rooms")
                            Text(
                                "Create a room or join with a code!",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
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
fun CreateRoomDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Room") },
        text = {
            Column {
                Text("Room settings would go here")
                // Add room configuration UI
            }
        },
        confirmButton = {
            Button(onClick = {
                // Create room logic
                onCreate("room-123")
            }) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
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
        title = { Text("Join Room") },
        text = {
            OutlinedTextField(
                value = roomCode,
                onValueChange = { roomCode = it.uppercase() },
                label = { Text("Room Code") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (roomCode.isNotBlank()) {
                        onJoin(roomCode)
                    }
                },
                enabled = roomCode.length == 6
            ) {
                Text("Join")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
