package com.example.e_wholesaler.main.users.owner.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Preview(showBackground = true)
@Composable
fun OwnerScreen() {

    val navCon = rememberNavController()

    NavHost(navController = navCon, startDestination = "OwnerHomeScreen") {
        composable("OwnerHomeScreen") {
            OwnerHomeScreen(navCon)
        }
    }

}


@Preview(showBackground = true)
@Composable
fun OwnerHomeScreen(navController: NavHostController = rememberNavController()) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar()
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Greeting Section
            Text(
                text = "Welcome Back, Sagar Matte",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Thursday, March 14, 2024",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items (getOwnerStats()) { stat ->
                    StatCard(stat)
                }
            }
        }
    }
}

// Top Bar
@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "E Wholesaler",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color(0xFF007BFF) // Blue color for branding
            ),
        )
        IconButton(
            onClick = { /* Handle Profile Click */ },
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle, // Replace with actual icon
                contentDescription = "Profile",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

// Stats Grid Data Model
data class OwnerStat(val title: String, val value: String, val isHighlighted: Boolean = false, val extraInfo: String? = null)

// Dummy Data
fun getOwnerStats(): List<OwnerStat> {
    return listOf(
        OwnerStat("Daily Revenue", "₹12,458", isHighlighted = true),
        OwnerStat("Active Orders", "284"),
        OwnerStat("Total Shops", "156"),
        OwnerStat("Available Workers", "89", extraInfo = "42 active now")
    )
}

// Stat Card UI
@Composable
fun StatCard(stat: OwnerStat) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.3f),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (stat.isHighlighted) Color(0xFF007BFF) else Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stat.title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (stat.isHighlighted) Color.White else Color.Gray
            )
            Text(
                text = stat.value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (stat.isHighlighted) Color.White else Color.Black
            )

            // Extra Info (Workers Status)
            stat.extraInfo?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person, // Replace with actual icon
                        contentDescription = "Workers",
                        tint = Color(0xFFFF9800), // Orange color
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(it, color = Color(0xFFFF9800), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

// Bottom Navigation Bar
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar(
        containerColor = Color.White
    ) {
        val items = listOf("Home", "Products", "Workers", "Orders")
        val icons = listOf(Icons.Default.Home, Icons.Filled.List, Icons.Default.Person, Icons.Default.ShoppingCart)

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == 0, // Default to Home selected
                onClick = { /* Handle navigation */ },
                icon = { Icon(imageVector = icons[index], contentDescription = item) },
                label = { Text(item) }
            )
        }
    }
}
