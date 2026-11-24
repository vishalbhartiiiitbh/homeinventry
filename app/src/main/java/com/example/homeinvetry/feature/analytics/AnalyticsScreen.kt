package com.example.homeinvetry.feature.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homeinvetry.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Household Analytics & Insights", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppBackground)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Spending Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = LightGreen),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Monthly Grocery Spending", style = MaterialTheme.typography.labelMedium.copy(color = PrimaryGreenDark))
                    Text(
                        "$184.50",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = PrimaryGreenDark)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("↓ 12% lower than last month", style = MaterialTheme.typography.labelSmall.copy(color = PrimaryGreenDark))
                }
            }

            // Spending by Category Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Outlined.PieChart, contentDescription = null, tint = PrimaryGreen)
                        Text("Category Spending Breakdown", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }

                    AnalyticsProgressRow("Rice & Grains", "$65.00", 0.35f, LightGreen, PrimaryGreen)
                    AnalyticsProgressRow("Cooking Oils & Condiments", "$42.50", 0.23f, LightYellow, Color(0xFFE67E22))
                    AnalyticsProgressRow("Detergents & Cleaning", "$38.00", 0.20f, LightBlue, Color(0xFF2980B9))
                    AnalyticsProgressRow("Dairy & Vegetables", "$39.00", 0.22f, LightRed, Color(0xFFE74C3C))
                }
            }

            // Most Consumed Products
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Outlined.TrendingUp, contentDescription = null, tint = PrimaryGreen)
                        Text("Most Frequently Consumed Products", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }

                    ListItemText("1. Basmati Rice", "Consumed 15 kg this month")
                    ListItemText("2. Whole Milk", "Consumed 12 Litres this month")
                    ListItemText("3. Detergent Powder", "Consumed 3 Packs this month")
                }
            }

            // Waste / Expiry Log
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = LightYellow),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Waste & Expiry Tracking", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("Zero food waste recorded this week! Keep using FEFO stock consumption.", style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText))
                }
            }
        }
    }
}

@Composable
fun AnalyticsProgressRow(
    category: String,
    amount: String,
    progress: Float,
    bgColor: Color,
    barColor: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(category, style = MaterialTheme.typography.bodyMedium)
            Text(amount, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = barColor,
            trackColor = bgColor
        )
    }
}

@Composable
fun ListItemText(title: String, subtitle: String) {
    Column {
        Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText))
    }
}
