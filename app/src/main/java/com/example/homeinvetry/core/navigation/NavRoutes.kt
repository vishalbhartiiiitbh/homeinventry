package com.example.homeinvetry.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Inventory : Screen("inventory", "Inventory", Icons.Filled.Inventory, Icons.Outlined.Inventory)
    object Shopping : Screen("shopping", "Shopping", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart)
    object Reminders : Screen("reminders", "Reminders", Icons.Filled.Alarm, Icons.Outlined.Alarm)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)

    // Sub-screens
    object AddItem : Screen("add_item", "Add Item")
    object ItemDetail : Screen("item_detail/{itemId}", "Item Details") {
        fun createRoute(itemId: String) = "item_detail/$itemId"
    }
    object Search : Screen("search", "Search")
    object AddReminder : Screen("add_reminder", "Add Reminder")
    object Analytics : Screen("analytics", "Analytics")
}

val bottomNavScreens = listOf(
    Screen.Home,
    Screen.Inventory,
    Screen.Shopping,
    Screen.Reminders,
    Screen.Settings
)
