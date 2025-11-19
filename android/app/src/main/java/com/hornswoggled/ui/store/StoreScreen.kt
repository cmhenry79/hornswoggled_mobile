package com.hornswoggled.ui.store

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Store") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    // Coin balance
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.MonetizationOn,
                                contentDescription = "Coins",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = coins.value.toString(),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Category Tabs
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                categories.forEach { category ->
                    Tab(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        text = {
                            Text(
                                text = category.replaceFirstChar { it.uppercase() },
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
            title = { Text("Purchase ${selectedItem!!.name}?") },
            text = {
                Column {
                    Text("This will cost ${selectedItem!!.price} coins.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Your balance: ${coins.value} coins",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (coins.value >= selectedItem!!.price) {
                            coins.value -= selectedItem!!.price
                            showPurchaseDialog = false
                            // In production: Call API to purchase item
                        }
                    },
                    enabled = coins.value >= selectedItem!!.price
                ) {
                    Text("Purchase")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPurchaseDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun StoreItemCard(
    item: StoreItemData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon/Emoji
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.emoji,
                    fontSize = 48.sp
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Chip(
                    text = item.category,
                    color = when (item.category) {
                        "avatar" -> Color(0xFF4CAF50)
                        "frame" -> Color(0xFF2196F3)
                        "badge" -> Color(0xFFFF9800)
                        "emote" -> Color(0xFF9C27B0)
                        else -> MaterialTheme.colorScheme.primary
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.MonetizationOn,
                            contentDescription = "Price",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.price.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Chip(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.2f),
        contentColor = color,
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(
            text = text.uppercase(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

data class StoreItemData(
    val id: String,
    val name: String,
    val category: String,
    val price: Int,
    val emoji: String
)
