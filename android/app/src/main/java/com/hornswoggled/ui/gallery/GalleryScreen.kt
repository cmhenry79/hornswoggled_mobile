package com.hornswoggled.ui.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hornswoggled.ui.components.*
import com.hornswoggled.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    onNavigateBack: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedItem by remember { mutableStateOf<GalleryItemData?>(null) }

    // Mock gallery items
    val galleryItems = remember {
        listOf(
            GalleryItemData("1", "My best definition", "text", "Happiness is...", 45, "2024-01-15"),
            GalleryItemData("2", "Funny moment", "image", "😂", 32, "2024-01-14"),
            GalleryItemData("3", "Creative answer", "text", "Love means...", 28, "2024-01-13"),
            GalleryItemData("4", "Epic win", "doodle", "🎨", 56, "2024-01-12"),
            GalleryItemData("5", "Classic response", "text", "Freedom is...", 41, "2024-01-11"),
            GalleryItemData("6", "Hilarious GIF", "gif", "🎬", 38, "2024-01-10"),
        )
    }

    val filters = listOf("All", "text", "image", "gif", "doodle")

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
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                HornswoggledCyan,
                                ElectricBlue
                            )
                        )
                    )
                    .padding(top = 48.dp, bottom = 24.dp)
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
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
                            text = "🖼️ My Gallery",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = { /* Share gallery */ },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White
                        )
                    }
                }
            }

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                text = when (filter) {
                                    "All" -> "🎭 All"
                                    "text" -> "📝 Text"
                                    "image" -> "📷 Images"
                                    "gif" -> "🎬 GIFs"
                                    "doodle" -> "🎨 Doodles"
                                    else -> filter
                                },
                                fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = HornswoggledCyan,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Gallery Grid
            val filteredItems = if (selectedFilter == "All") {
                galleryItems
            } else {
                galleryItems.filter { it.type == selectedFilter }
            }

            if (filteredItems.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    GameCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🎭", fontSize = 80.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No submissions saved",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Save your favorite submissions during games!",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredItems) { item ->
                        GalleryItemCard(
                            item = item,
                            onClick = { selectedItem = item }
                        )
                    }
                }
            }
        }
    }

    // Item Detail Dialog
    selectedItem?.let { item ->
        Dialog(onDismissRequest = { selectedItem = null }) {
            GameCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Type Badge
                    Surface(
                        color = getTypeColor(item.type),
                        shape = PillShape
                    ) {
                        Text(
                            text = when (item.type) {
                                "text" -> "📝 " + item.type.uppercase()
                                "image" -> "📷 " + item.type.uppercase()
                                "gif" -> "🎬 " + item.type.uppercase()
                                "doodle" -> "🎨 " + item.type.uppercase()
                                else -> item.type.uppercase()
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    Text(
                        text = item.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Content Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                getTypeColor(item.type).copy(alpha = 0.1f),
                                GameCardShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.contentPreview,
                            fontSize = if (item.type == "text") 18.sp else 64.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp),
                            fontWeight = if (item.type == "text") FontWeight.Medium else FontWeight.Normal
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = HornswoggledPurple.copy(alpha = 0.2f)
                            ) {
                                Box(
                                    modifier = Modifier.size(56.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("👍", fontSize = 24.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${item.votes}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Votes",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = HornswoggledCyan.copy(alpha = 0.2f)
                            ) {
                                Box(
                                    modifier = Modifier.size(56.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("📅", fontSize = 24.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.date,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Saved",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GameOutlinedButton(
                            text = "📤 Share",
                            onClick = { /* Share item */ },
                            modifier = Modifier.weight(1f)
                        )

                        GameButton(
                            text = "Close",
                            onClick = { selectedItem = null },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GalleryItemCard(
    item: GalleryItemData,
    onClick: () -> Unit
) {
    GameCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Content Preview
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(getTypeColor(item.type).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.contentPreview,
                    fontSize = if (item.type == "text") 14.sp else 40.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp),
                    maxLines = if (item.type == "text") 3 else 1,
                    fontWeight = if (item.type == "text") FontWeight.Medium else FontWeight.Normal
                )
            }

            // Type Badge
            Surface(
                color = getTypeColor(item.type),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                shape = PillShape
            ) {
                Text(
                    text = when (item.type) {
                        "text" -> "📝"
                        "image" -> "📷"
                        "gif" -> "🎬"
                        "doodle" -> "🎨"
                        else -> "📄"
                    },
                    modifier = Modifier.padding(6.dp),
                    fontSize = 14.sp
                )
            }

            // Votes Badge
            Surface(
                color = HornswoggledPurple,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                shape = PillShape
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("👍", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.votes.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

private fun getTypeColor(type: String): Color {
    return when (type) {
        "text" -> Color(0xFF2196F3)
        "image" -> Color(0xFF4CAF50)
        "gif" -> Color(0xFFFF9800)
        "doodle" -> Color(0xFF9C27B0)
        else -> Color.Gray
    }
}

data class GalleryItemData(
    val id: String,
    val title: String,
    val type: String,
    val contentPreview: String,
    val votes: Int,
    val date: String
)
