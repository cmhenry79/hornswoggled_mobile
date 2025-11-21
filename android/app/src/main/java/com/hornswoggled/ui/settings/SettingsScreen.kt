package com.hornswoggled.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hornswoggled.ui.components.*
import com.hornswoggled.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    var soundEnabled by remember { mutableStateOf(true) }
    var musicEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var autoSaveEnabled by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogType by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Hero Header with Gradient
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
                        .padding(top = 48.dp, bottom = 24.dp)
                        .padding(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "⚙️ Settings",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            // Account Section
            item {
                SectionHeader("👤 Account")
            }

            item {
                GameCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.Person,
                            title = "Edit Profile",
                            subtitle = "Change your display name and avatar",
                            onClick = { /* Navigate to edit profile */ }
                        )

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        SettingsItem(
                            icon = Icons.Default.Email,
                            title = "Email",
                            subtitle = "user@example.com",
                            onClick = { /* Change email */ }
                        )
                    }
                }
            }

            // Game Settings
            item {
                SectionHeader("🎮 Game")
            }

            item {
                GameCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column {
                        SwitchSettingsItem(
                            icon = Icons.Default.VolumeUp,
                            title = "Sound Effects",
                            subtitle = "Play sound effects during game",
                            checked = soundEnabled,
                            onCheckedChange = { soundEnabled = it }
                        )

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        SwitchSettingsItem(
                            icon = Icons.Default.MusicNote,
                            title = "Music",
                            subtitle = "Play background music",
                            checked = musicEnabled,
                            onCheckedChange = { musicEnabled = it }
                        )

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        SwitchSettingsItem(
                            icon = Icons.Default.Vibration,
                            title = "Vibration",
                            subtitle = "Vibrate on interactions",
                            checked = vibrationEnabled,
                            onCheckedChange = { vibrationEnabled = it }
                        )

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        SwitchSettingsItem(
                            icon = Icons.Default.Save,
                            title = "Auto-save Submissions",
                            subtitle = "Automatically save submissions to gallery",
                            checked = autoSaveEnabled,
                            onCheckedChange = { autoSaveEnabled = it }
                        )
                    }
                }
            }

            // Notifications
            item {
                SectionHeader("🔔 Notifications")
            }

            item {
                GameCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    SwitchSettingsItem(
                        icon = Icons.Default.Notifications,
                        title = "Push Notifications",
                        subtitle = "Receive notifications about games and friends",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                }
            }

            // About Section
            item {
                SectionHeader("ℹ️ About")
            }

            item {
                GameCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.Info,
                            title = "App Version",
                            subtitle = "1.0.0",
                            onClick = { }
                        )

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        SettingsItem(
                            icon = Icons.Default.Description,
                            title = "Terms of Service",
                            subtitle = "Read our terms",
                            onClick = {
                                dialogType = "terms"
                                showDialog = true
                            }
                        )

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        SettingsItem(
                            icon = Icons.Default.PrivacyTip,
                            title = "Privacy Policy",
                            subtitle = "How we handle your data",
                            onClick = {
                                dialogType = "privacy"
                                showDialog = true
                            }
                        )

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        SettingsItem(
                            icon = Icons.Default.Help,
                            title = "Help & Support",
                            subtitle = "Get help with Hornswoggled",
                            onClick = { /* Open support */ }
                        )
                    }
                }
            }

            // Danger Zone
            item {
                SectionHeader("⚠️ Danger Zone")
            }

            item {
                GameCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.DeleteForever,
                            title = "Delete Account",
                            subtitle = "Permanently delete your account",
                            onClick = {
                                dialogType = "delete"
                                showDialog = true
                            },
                            isDangerous = true
                        )

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        SettingsItem(
                            icon = Icons.Default.ExitToApp,
                            title = "Logout",
                            subtitle = "Sign out of your account",
                            onClick = {
                                dialogType = "logout"
                                showDialog = true
                            }
                        )
                    }
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Confirmation Dialogs
    if (showDialog) {
        when (dialogType) {
            "logout" -> {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    containerColor = MaterialTheme.colorScheme.surface,
                    title = {
                        Text(
                            "🚪 Logout",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = { Text("Are you sure you want to logout?") },
                    confirmButton = {
                        GameButton(
                            text = "Logout",
                            onClick = {
                                showDialog = false
                                // Perform logout
                            }
                        )
                    },
                    dismissButton = {
                        GameOutlinedButton(
                            text = "Cancel",
                            onClick = { showDialog = false }
                        )
                    }
                )
            }

            "delete" -> {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    containerColor = MaterialTheme.colorScheme.surface,
                    title = {
                        Text(
                            "⚠️ Delete Account",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )
                    },
                    text = {
                        Text("This action cannot be undone. All your data will be permanently deleted.")
                    },
                    confirmButton = {
                        GameButton(
                            text = "Delete",
                            onClick = {
                                showDialog = false
                                // Perform account deletion
                            },
                            gradient = Brush.horizontalGradient(
                                colors = listOf(ErrorRed, ErrorRed)
                            )
                        )
                    },
                    dismissButton = {
                        GameOutlinedButton(
                            text = "Cancel",
                            onClick = { showDialog = false }
                        )
                    }
                )
            }

            "terms", "privacy" -> {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    containerColor = MaterialTheme.colorScheme.surface,
                    title = {
                        Text(
                            if (dialogType == "terms") "📄 Terms of Service" else "🔒 Privacy Policy",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            "This would display the full ${if (dialogType == "terms") "terms of service" else "privacy policy"} content.\n\n" +
                                    "In production, this would show the actual legal documents."
                        )
                    },
                    confirmButton = {
                        GameButton(
                            text = "Close",
                            onClick = { showDialog = false }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = HornswoggledPurple,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp, top = 24.dp)
    )
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDangerous: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = if (isDangerous) ErrorRed else MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = if (isDangerous) ErrorRed else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = "Go",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun SwitchSettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = if (checked) HornswoggledPurple else MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = HornswoggledPurple,
                checkedBorderColor = HornswoggledPurple
            )
        )
    }
}
