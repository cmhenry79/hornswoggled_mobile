package com.hornswoggled.ui.store

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hornswoggled.ui.components.*
import com.hornswoggled.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    onNavigateBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var showPurchaseDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<StoreItemData?>(null) }
    val coins = remember { mutableStateOf(1250) }

    // Mock store items
    val storeItems = remember {
        listOf(
            StoreItemData("1", "Cool Avatar", "avatar", 100, "😎"),
            StoreItemData("2", "Gold Frame", "frame", 250, "🖼️"),
            StoreItemData("3", "Winner Badge", "badge", 150, "🏆"),
            StoreItemData("4", "Star Emote", "emote", 50, "⭐"),
            StoreItemData("5", "Fire Avatar", "avatar", 200, "🔥"),
            StoreItemData("6", "Rainbow Frame", "frame", 300, "🌈"),
            StoreItemData("7", "Expert Badge", "badge", 500, "🎓"),
            StoreItemData("8", "Heart Emote", "emote", 75, "❤️"),
        )
    }

    val categories = listOf("All", "avatar", "frame", "badge", "emote")

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
                                HornswoggledOrange,
                                HornswoggledYellow
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
                            text = "🏪 Store",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    // Coin balance
                    ScoreBadge(score = coins.value)
                }
            }

            // Category Tabs
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = HornswoggledPurple
            ) {
                categories.forEach { category ->
                    Tab(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        text = {
                            Text(
                                text = when (category) {
                                    "All" -> "🎭 All"
                                    "avatar" -> "👤 Avatars"
                                    "frame" -> "🖼️ Frames"
                                    "badge" -> "🏆 Badges"
                                    "emote" -> "💬 Emotes"
                                    else -> category
                                },
                                fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Store Items Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val filteredItems = if (selectedCategory == "All") {
                    storeItems
                } else {
                    storeItems.filter { it.category == selectedCategory }
                }

                items(filteredItems) { item ->
                    StoreItemCard(
                        item = item,
                        onClick = {
                            selectedItem = item
                            showPurchaseDialog = true
                        }
                    )
                }
            }
        }
    }

    // Purchase Dialog
    if (showPurchaseDialog && selectedItem != null) {
        AlertDialog(
            onDismissRequest = { showPurchaseDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    "🛒 Purchase ${selectedItem!!.name}?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    // Item preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                getCategoryColor(selectedItem!!.category).copy(alpha = 0.1f),
                                shape = GameCardShape
                            )
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = selectedItem!!.emoji,
                            fontSize = 64.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = GameCardShape,
                        color = InfoBlue.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, InfoBlue.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Cost:", fontSize = 14.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("⭐", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "${selectedItem!!.price}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Your balance:", fontSize = 14.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("⭐", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "${coins.value}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (coins.value >= selectedItem!!.price) SuccessGreen else ErrorRed
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                GameButton(
                    text = "Purchase",
                    onClick = {
                        if (coins.value >= selectedItem!!.price) {
                            coins.value -= selectedItem!!.price
                            showPurchaseDialog = false
                        }
                    },
                    enabled = coins.value >= selectedItem!!.price,
                    gradient = Brush.horizontalGradient(
                        colors = listOf(SuccessGreen, Color(0xFF00C853))
                    )
                )
            },
            dismissButton = {
                GameOutlinedButton(
                    text = "Cancel",
                    onClick = { showPurchaseDialog = false }
                )
            }
        )
    }
}

@Composable
private fun StoreItemCard(
    item: StoreItemData,
    onClick: () -> Unit
) {
    GameCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon/Emoji
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        getCategoryColor(item.category).copy(alpha = 0.2f),
                        shape = GameCardShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.emoji,
                    fontSize = 48.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Category chip
            Surface(
                color = getCategoryColor(item.category).copy(alpha = 0.2f),
                shape = PillShape
            ) {
                Text(
                    text = item.category.uppercase(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = getCategoryColor(item.category)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Price
            Surface(
                color = HornswoggledYellow,
                shape = PillShape
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⭐", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.price.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF2D1B00)
                    )
                }
            }
        }
    }
}

private fun getCategoryColor(category: String): Color {
    return when (category) {
        "avatar" -> Color(0xFF4CAF50)
        "frame" -> Color(0xFF2196F3)
        "badge" -> Color(0xFFFF9800)
        "emote" -> Color(0xFF9C27B0)
        else -> Color.Gray
    }
}

data class StoreItemData(
    val id: String,
    val name: String,
    val category: String,
    val price: Int,
    val emoji: String
)
