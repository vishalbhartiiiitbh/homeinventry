package com.example.homeinvetry

import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.homeinvetry.core.navigation.HomeStockBottomBar
import com.example.homeinvetry.core.navigation.Screen
import com.example.homeinvetry.core.navigation.bottomNavScreens
import com.example.homeinvetry.core.notifications.NotificationHelper
import com.example.homeinvetry.feature.analytics.AnalyticsScreen
import com.example.homeinvetry.feature.dashboard.HomeScreen
import com.example.homeinvetry.feature.inventory.AddItemScreen
import com.example.homeinvetry.feature.inventory.InventoryScreen
import com.example.homeinvetry.feature.inventory.ItemDetailScreen
import com.example.homeinvetry.feature.reminders.RemindersScreen
import com.example.homeinvetry.feature.search.SearchScreen
import com.example.homeinvetry.feature.settings.SettingsScreen
import com.example.homeinvetry.feature.shopping.ShoppingScreen
import com.example.homeinvetry.ui.theme.HomeStockTheme
import com.example.homeinvetry.ui.viewmodel.HomeStockViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeStockTheme {
                HomeStockApp()
            }
        }
    }
}

@Composable
fun HomeStockApp() {
    val context = LocalContext.current.applicationContext as Application
    val viewModelFactory = remember(context) { HomeStockViewModelFactory(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            NotificationHelper.showNotification(
                context,
                1001,
                "HomeStock Notifications Enabled 🔔",
                "You will receive live device alerts for low stock, expiries & tasks."
            )
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            NotificationHelper.showNotification(
                context,
                1001,
                "Welcome to HomeStock 📦",
                "Your household inventory is active and being monitored."
            )
        }
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val shouldShowBottomBar = bottomNavScreens.any { screen ->
        currentRoute == screen.route || currentRoute?.startsWith("${screen.route}?") == true
    }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                HomeStockBottomBar(navController = navController)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Home Dashboard
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToInventory = { filter ->
                        navController.navigate("${Screen.Inventory.route}?filter=${filter ?: ""}")
                    },
                    onNavigateToShopping = { navController.navigate(Screen.Shopping.route) },
                    onNavigateToReminders = { navController.navigate(Screen.Reminders.route) },
                    onNavigateToAddItem = { navController.navigate(Screen.AddItem.route) },
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onNavigateToItemDetail = { itemId ->
                        navController.navigate(Screen.ItemDetail.createRoute(itemId))
                    },
                    dashboardViewModel = viewModel(factory = viewModelFactory)
                )
            }

            // 2. Inventory Management
            composable(
                route = "${Screen.Inventory.route}?filter={filter}",
                arguments = listOf(navArgument("filter") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val filter = backStackEntry.arguments?.getString("filter")
                InventoryScreen(
                    initialFilter = filter,
                    onNavigateToAddItem = { selectedLoc ->
                        navController.navigate("${Screen.AddItem.route}?location=${selectedLoc}")
                    },
                    onNavigateToItemDetail = { itemId ->
                        navController.navigate(Screen.ItemDetail.createRoute(itemId))
                    },
                    inventoryViewModel = viewModel(factory = viewModelFactory)
                )
            }

            // 3. Shopping Management
            composable(Screen.Shopping.route) {
                ShoppingScreen(
                    onNavigateBack = { navController.popBackStack() },
                    shoppingViewModel = viewModel(factory = viewModelFactory)
                )
            }

            // 4. Reminders & Household Tasks
            composable(Screen.Reminders.route) {
                RemindersScreen(
                    onNavigateBack = { navController.popBackStack() },
                    remindersViewModel = viewModel(factory = viewModelFactory)
                )
            }

            // 5. Settings & Config
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) }
                )
            }

            // Sub-screen: Add Item
            composable(
                route = "${Screen.AddItem.route}?location={location}",
                arguments = listOf(navArgument("location") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val initialLocation = backStackEntry.arguments?.getString("location")
                AddItemScreen(
                    initialLocationName = initialLocation,
                    onNavigateBack = { navController.popBackStack() },
                    onItemSaved = { newItem ->
                        navController.popBackStack()
                    },
                    addItemViewModel = viewModel(factory = viewModelFactory)
                )
            }

            // Sub-screen: Item Details
            composable(
                route = Screen.ItemDetail.route,
                arguments = listOf(navArgument("itemId") { type = NavType.StringType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                ItemDetailScreen(
                    itemId = itemId,
                    onNavigateBack = { navController.popBackStack() },
                    itemDetailViewModel = viewModel(factory = viewModelFactory)
                )
            }

            // Sub-screen: Global Search
            composable(Screen.Search.route) {
                SearchScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToItemDetail = { itemId ->
                        navController.navigate(Screen.ItemDetail.createRoute(itemId))
                    }
                )
            }

            // Sub-screen: Household Analytics
            composable(Screen.Analytics.route) {
                AnalyticsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
