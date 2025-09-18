package com.example.e_wholesaler.main.users.owner.ui

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.e_wholesaler.main.users.owner.dtos.HomeScreenDetails
import com.example.e_wholesaler.main.users.owner.viewmodels.OwnerViewModel
import com.example.e_wholesaler.navigation_viewmodel.NavigationViewModel
import com.example.ui.OwnerInfoScreen
import org.koin.androidx.compose.koinViewModel


@Composable
fun getViewModelStoreOwner() = LocalActivity.current as ViewModelStoreOwner

@Composable
fun getIsPreview() = LocalInspectionMode.current

@SuppressLint("ContextCastToActivity")
@Composable
fun OwnerScreen() {

    val navCon = rememberNavController()
    val navigationViewModel = if (!getIsPreview()) {
        koinViewModel<NavigationViewModel>(
            viewModelStoreOwner = getViewModelStoreOwner()
        )
    } else null
    val ownerViewModel = koinViewModel<OwnerViewModel>(
        viewModelStoreOwner = getViewModelStoreOwner()
    )
    val details by ownerViewModel.detailsFlow.collectAsState(null)

    LaunchedEffect(Unit) {
        navigationViewModel?.addController("OwnerController", navCon)
    }

    NavHost(navController = navCon, startDestination = "OwnerHomeScreen") {
        composable("OwnerHomeScreen") {
            OwnerHomeScreen(
                navCon,
                refreshStats = { ownerViewModel.getHomeScreenDetails() },
                details?.ownerDetails?.name.toString(),
                details?.homeScreenDetails
            )
        }

        composable("OwnerInfoScreen") {
            OwnerInfoScreen(details?.ownerDetails)
        }

        composable("RevenueScreen") {
            RevenueScreen()
        }

        composable("ShopsScreen") {
            ShopsScreen()
        }
    }

}


@Preview(showBackground = true)
@Composable
fun OwnerHomeScreenPreview() {
    OwnerHomeScreen(rememberNavController(), {}, "null", null)
}

@SuppressLint("ContextCastToActivity")
@Composable
fun OwnerHomeScreen(navController: NavHostController, refreshStats: () -> Unit, ownerName: String, homeScreenDetails: HomeScreenDetails?) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
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
                    onClick = {
                        navController.navigate("OwnerInfoScreen")
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle, // Replace with actual icon
                        contentDescription = "Profile",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
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
            Box(
                modifier = Modifier.fillMaxWidth(),
            ){
                Text(
                    text = "Welcome Back, $ownerName",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { refreshStats() },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "refresh stats")
                }
            }
            Text(
                text = "Thursday, March 14, 2024",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            val navigationViewModel = if (!getIsPreview()) {
                koinViewModel<NavigationViewModel>(
                    viewModelStoreOwner = getViewModelStoreOwner()
                )
            } else null
            val ownerViewModel = koinViewModel<OwnerViewModel>(
                viewModelStoreOwner = getViewModelStoreOwner()
            )
            // Stats Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items (getOwnerStats(homeScreenDetails)) { stat ->
                    StatCard(stat, navigationViewModel, ownerViewModel)
                }
            }
        }
    }
}

// Stats Grid Data Model
data class OwnerStat(
    val title: String,
    val value: String,
    val isHighlighted: Boolean = false,
    val extraInfo: String? = null
)

// Dummy Data
fun getOwnerStats(homeScreenDetails: HomeScreenDetails?): List<OwnerStat> =  listOf(
    OwnerStat("Daily Revenue", "Rs. ${homeScreenDetails?.salesAmount.toString()}", isHighlighted = true),
    OwnerStat("Active Orders", homeScreenDetails?.creatingOrderCount.toString()),
    OwnerStat("Total Shops", homeScreenDetails?.shopCount.toString()),
    OwnerStat("Available Workers", homeScreenDetails?.workerCount.toString())
)

// Stat Card UI
@Composable
fun StatCard(
    stat: OwnerStat,
    viewModel: NavigationViewModel?,
    ownerViewModel: OwnerViewModel?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.3f)
            .clickable {
                val navController = viewModel?.getController("OwnerController")
                when (stat.title) {
                    "Daily Revenue" -> {
                        ownerViewModel?.getDailyRevenue()
                        navController?.navigate("RevenueScreen")
                    }

                    "Active Orders" -> TODO()
                    "Total Shops" -> {
                        ownerViewModel?.getOwnerShops()
                        navController?.navigate("ShopsScreen")
                    }

                    "Available Workers" -> TODO()
                }
            },
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
        val icons = listOf(
            Icons.Default.Home,
            Icons.AutoMirrored.Filled.List,
            Icons.Default.Person,
            Icons.Default.ShoppingCart
        )

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
