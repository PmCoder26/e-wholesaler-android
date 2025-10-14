package com.example.e_wholesaler.main.users.owner.ui.owner

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.e_wholesaler.main.users.owner.dtos.HomeScreenDetails
import com.example.e_wholesaler.main.users.owner.dtos.Product
import com.example.e_wholesaler.main.users.owner.dtos.Shop
import com.example.e_wholesaler.main.users.owner.dtos.hasDifferentData
import com.example.e_wholesaler.main.users.owner.dtos.hasNoBlankField
import com.example.e_wholesaler.main.users.owner.ui.products.ProductDetailsScreen
import com.example.e_wholesaler.main.users.owner.ui.products.ShopProductsScreen
import com.example.e_wholesaler.main.users.owner.viewmodels.NullOwnerViewModel
import com.example.e_wholesaler.main.users.owner.viewmodels.OwnerViewModel
import com.example.e_wholesaler.main.users.owner.viewmodels.utils.Details
import com.example.e_wholesaler.navigation_viewmodel.NavigationViewModel
import com.example.e_wholesaler.navigation_viewmodel.NullNavigationViewModel
import com.example.ui.OwnerInfoScreen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun getViewModelStoreOwner() = LocalActivity.current as ViewModelStoreOwner

@Composable
fun getIsPreview() = LocalInspectionMode.current


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ContextCastToActivity", "ProduceStateDoesNotAssignValue", "UnrememberedMutableState")
@Composable
fun OwnerScreen() {

    val navCon = rememberNavController()
    val navigationViewModel = if (!getIsPreview()) {
        koinViewModel<NavigationViewModel>(
            viewModelStoreOwner = getViewModelStoreOwner()
        )
    } else NullNavigationViewModel()
    val ownerViewModel = if (!getIsPreview()) {
        koinViewModel<OwnerViewModel>(
            viewModelStoreOwner = getViewModelStoreOwner()
        )
    } else NullOwnerViewModel()
    val details by ownerViewModel.detailsFlow.collectAsState(Details())
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        navigationViewModel.addController("OwnerController", navCon)
    }

    NavHost(navController = navCon, startDestination = "OwnerHomeScreen") {
        composable("OwnerHomeScreen") {
            OwnerHomeScreen(
                navCon,
                refreshStats = {
                    scope.launch {
                        ownerViewModel.getHomeScreenDetails()
                    }
                },
                details.ownerDetails?.name.toString(),
                details.homeScreenDetails
            )
        }

        composable("OwnerInfoScreen") {
            OwnerInfoScreen(details.ownerDetails)
        }

        composable("RevenueScreen") {
            _root_ide_package_.com.example.e_wholesaler.main.users.owner.ui.RevenueScreen()
        }

        composable("ShopsScreen") {
            _root_ide_package_.com.example.e_wholesaler.main.users.owner.ui.ShopsScreen()
        }

        composable(
            route = "ShopDetailsScreen/{shopId}",
            arguments = listOf(
                navArgument("shopId") { type = NavType.LongType }
            )
        ) {
            val currentShopId = it.arguments?.getLong("shopId") ?: -1
            val currentShop by produceState(initialValue = Shop(), currentShopId) {
                value = ownerViewModel.getShopById(currentShopId)
            }

            _root_ide_package_.com.example.e_wholesaler.main.users.owner.ui.ShopDetailsScreen(
                shopDetail = currentShop,
                onBackClicked = { navCon.popBackStack() },
                onEditDetailsClicked = { navCon.navigate("EditShopDetailsScreen/$currentShopId") }
            )
        }

        composable(
            route = "EditShopDetailsScreen/{shopId}",
            arguments = listOf(
                navArgument("shopId") { type = NavType.LongType }
            )
        ) {
            val currentShopId = it.arguments?.getLong("shopId") ?: -1
            val currentShop by produceState(initialValue = Shop(), currentShopId) {
                value = ownerViewModel.getShopById(currentShopId)
            }

            _root_ide_package_.com.example.e_wholesaler.main.users.owner.ui.EditShopDetailsScreen(
                shop = currentShop,
                onBackClicked = { navCon.popBackStack() },
                onSaveClicked = { changedShop ->
                    scope.launch {
                        if (changedShop.hasNoBlankField() && changedShop.hasDifferentData(
                                currentShop
                            )
                        ) {
                            val hasDataUpdated = ownerViewModel.updateShopDetails(changedShop)
                            val message =
                                if (hasDataUpdated) "Shop details edited successfully" else "Failed to update shop details"
                            showToast(context, message)
                            if (hasDataUpdated) navCon.popBackStack()
                        }
                    }
                }
            )
        }

        composable("AddShopScreen") {
            _root_ide_package_.com.example.e_wholesaler.main.users.owner.ui.AddShopScreen(
                onCancelClicked = { navCon.popBackStack() },
                onSaveClicked = { newShop ->
                    scope.launch {
                        if (newShop.hasNoBlankField()) {
                            val hasAddedNewShop = ownerViewModel.addNewShop(newShop)
                            val message =
                                if (hasAddedNewShop) "New shop added successfully" else "Unable to add the new shop"
                            showToast(context, message)
                        } else {
                            showToast(context, "Please fill all the shop details")
                        }
                    }
                }
            )
        }

        composable("ShopProductsScreen") {
            val shopsState by ownerViewModel.shopsState.collectAsState()
            val shopProductsState by ownerViewModel.shopProductsState.collectAsState()

            ShopProductsScreen(
                shops = shopsState.shops,
                initialSelectedShop = shopsState.shops.firstOrNull(),
                products = shopProductsState.products,
                onBackClicked = { navCon.popBackStack() },
                onShopSelected = { ownerViewModel.getShopProducts(it) },
                onAddProductClicked = { },
                onFilterChange = { ownerViewModel.updateProductSortType(it) },
                onInfoButtonClick = { clickedProduct -> navCon.navigate("ProductDetailsScreen/${clickedProduct.name}") }
            )
        }

        composable(
            route = "ProductDetailsScreen/{productName}",
            arguments = listOf(
                navArgument("productName") { type = NavType.StringType }
            )
        ) {
            val productName = it.arguments?.getString("productName") ?: ""
            var trigger by remember {
                mutableIntStateOf(0)
            }
            val selectedProduct by produceState(
                initialValue = Product(),
                key1 = productName,
                key2 = trigger
            ) {
                value = ownerViewModel.getProductByName(productName).copy()
            }

            ProductDetailsScreen(
                product = selectedProduct,
                onBackClicked = { navCon.popBackStack() },
                onEditSubProductConfirm = { subProduct ->
                    scope.launch {
                        val hasUpdatedSubProduct =
                            ownerViewModel.updateShopSubProduct(selectedProduct.name, subProduct)
                        val message =
                            if (hasUpdatedSubProduct) "Product variant updated successfully" else "Failed to update product variant"
                        showToast(context, message)
                        if (hasUpdatedSubProduct) trigger++
                    }
                },
                onAddSubProductConfirm = { subProduct ->
                    scope.launch {
                        val hasAddedSubProduct =
                            ownerViewModel.addShopSubProduct(selectedProduct.name, subProduct)
                        val message =
                            if (hasAddedSubProduct) "Product variant added successfully" else "Failed to add product variant"
                        showToast(context, message)
                        if (hasAddedSubProduct) trigger++
                    }
                },
                onDeleteSubProductConfirm = { subProduct ->
                    scope.launch {
                        val hasRemoved =
                            ownerViewModel.removeShopSubProduct(selectedProduct.name, subProduct)
                        val message =
                            if (hasRemoved) "Product removed successfully" else "Failed to remove product"
                        showToast(context, message)
                        if (hasRemoved) trigger++
                    }
                }
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun OwnerHomeScreenPreview() {
    OwnerHomeScreen(rememberNavController(), {}, "null", null)
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ContextCastToActivity")
@Composable
fun OwnerHomeScreen(
    navController: NavHostController,
    refreshStats: () -> Unit, ownerName: String,
    homeScreenDetails: HomeScreenDetails?
) {

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
            val ownerViewModel = if (!getIsPreview()) {
                koinViewModel<OwnerViewModel>(
                    viewModelStoreOwner = getViewModelStoreOwner()
                )
            } else NullOwnerViewModel()
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
@RequiresApi(Build.VERSION_CODES.O)
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
    var selectedIndex by remember { mutableIntStateOf(0) }

    val items = listOf("Home", "Products", "Workers", "Orders")
    val icons = listOf(
        Icons.Default.Home,
        Icons.AutoMirrored.Filled.List,
        Icons.Default.Person,
        Icons.Default.ShoppingCart
    )

    NavigationBar(containerColor = Color.White) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedIndex,
                onClick = {
                    selectedIndex = index
                    when (item) {
                        "Home" -> navController.navigate("HomeScreen")
                        "Products" -> navController.navigate("ShopProductsScreen")
                        "Workers" -> navController.navigate("WorkersScreen")
                        "Orders" -> navController.navigate("OrdersScreen")
                    }
                },
                icon = { Icon(imageVector = icons[index], contentDescription = item) },
                label = { Text(item) }
            )
        }
    }
}


fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}