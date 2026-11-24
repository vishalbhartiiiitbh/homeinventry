package com.example.homeinvetry.feature.shopping

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.homeinvetry.data.local.entity.ShoppingItemEntity
import com.example.homeinvetry.ui.theme.*
import com.example.homeinvetry.ui.viewmodel.ShoppingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    shoppingViewModel: ShoppingViewModel = viewModel()
) {
    val shoppingList by shoppingViewModel.shoppingItems.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }
    var newItemText by remember { mutableStateOf("") }

    val pendingItems = shoppingList.filter { !it.isPurchased }
    val purchasedItems = shoppingList.filter { it.isPurchased }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping List", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Outlined.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { /* More */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        // Mark all pending as purchased & update stock in Room
                        pendingItems.forEach { item ->
                            shoppingViewModel.markItemPurchasedAndAddStock(item, item.requiredQuantity, item.estimatedPrice)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mark as Purchased",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppBackground),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Filter Chips Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "All",
                        onClick = { selectedFilter = "All" },
                        label = { Text("All (${shoppingList.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    FilterChip(
                        selected = selectedFilter == "Pending",
                        onClick = { selectedFilter = "Pending" },
                        label = { Text("Pending (${pendingItems.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    FilterChip(
                        selected = selectedFilter == "Purchased",
                        onClick = { selectedFilter = "Purchased" },
                        label = { Text("Purchased (${purchasedItems.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            // Quick Add Item Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(LightGreen)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextField(
                        value = newItemText,
                        onValueChange = { newItemText = it },
                        placeholder = { Text("Add Item", color = PrimaryGreenDark) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            if (newItemText.isNotBlank()) {
                                shoppingViewModel.addShoppingItem(newItemText.trim())
                                newItemText = ""
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Pending Items List from Room
            if (selectedFilter != "Purchased") {
                items(pendingItems, key = { it.id }) { item ->
                    ShoppingRowCardEntity(
                        item = item,
                        onToggle = { shoppingViewModel.togglePurchased(item) },
                        onDelete = { shoppingViewModel.deleteShoppingItem(item.id) }
                    )
                }
            }

            // Purchased Section Header & Items from Room
            if (selectedFilter != "Pending" && purchasedItems.isNotEmpty()) {
                item {
                    Text(
                        text = "Purchased",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MainText
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(purchasedItems, key = { it.id }) { item ->
                    ShoppingRowCardEntity(
                        item = item,
                        onToggle = { shoppingViewModel.togglePurchased(item) },
                        onDelete = { shoppingViewModel.deleteShoppingItem(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ShoppingRowCardEntity(
    item: ShoppingItemEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Checkbox / Checked Icon
                IconButton(onClick = onToggle, modifier = Modifier.size(28.dp)) {
                    if (item.isPurchased) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Purchased",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Pending",
                            tint = SecondaryText,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Item Emoji Box
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LightYellow),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.iconEmoji, fontSize = 22.sp)
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "${item.name} (${item.requiredQuantity.toInt()} ${item.unit})",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None
                            )
                        )
                        if (item.isLowStockAlert) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = LightRed
                            ) {
                                Text(
                                    text = "Low Stock",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFE74C3C),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = SecondaryText,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
