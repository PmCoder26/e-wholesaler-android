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
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.e_wholesaler.main.users.owner.dtos.AddProductForShopRequest
import com.example.e_wholesaler.main.users.owner.dtos.AddSubProductsForShopRequest
import com.example.e_wholesaler.main.users.owner.dtos.HomeScreenDetails
import com.example.e_wholesaler.main.users.owner.dtos.ProductIdentity
import com.example.e_wholesaler.main.users.owner.dtos.SellingUnitRequest
import com.example.e_wholesaler.main.users.owner.dtos.Shop
import com.example.e_wholesaler.main.users.owner.dtos.SubProductRequest
import com.example.e_wholesaler.main.users.owner.dtos.Worker
import com.example.e_wholesaler.main.users.owner.dtos.hasDifferentData
import com.example.e_wholesaler.main.users.owner.dtos.hasNoBlankField
import com.example.e_wholesaler.main.users.owner.ui.products.AddProductScreen
import com.example.e_wholesaler.main.users.owner.ui.products.ProductDetailsScreen
import com.example.e_wholesaler.main.users.owner.ui.products.ShopProductsScreen
import com.example.e_wholesaler.main.users.owner.ui.revenue.RevenueScreen
import com.example.e_wholesaler.main.users.owner.ui.shops.AddShopScreen
import com.example.e_wholesaler.main.users.owner.ui.shops.EditShopDetailsScreen
import com.example.e_wholesaler.main.users.owner.ui.shops.ShopDetailsScreen
import com.example.e_wholesaler.main.users.owner.ui.shops.ShopsScreen
import com.example.e_wholesaler.main.users.owner.ui.workers.WorkerAddUpdateScreen
import com.example.e_wholesaler.main.users.owner.ui.workers.WorkerDetailsScreen
import com.example.e_wholesaler.main.users.owner.ui.workers.WorkersScreen
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
            RevenueScreen()
        }

        composable("ShopsScreen") {
            ShopsScreen()
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

            ShopDetailsScreen(
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

            EditShopDetailsScreen(
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
            AddShopScreen(
                onCancelClicked = { navCon.popBackStack() },
                onSaveClicked = { newShop ->
                    scope.launch {
                        if (newShop.hasNoBlankField()) {
                            val hasAddedNewShop = ownerViewModel.addNewShop(newShop)
                            val message =
                                if (hasAddedNewShop) "New shop added successfully" else "Unable to add the new shop"
                            showToast(context, message)
                            if (hasAddedNewShop) navCon.popBackStack()
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
                initialSelectedShop = shopProductsState.currentShop,
                products = shopProductsState.products,
                onBackClicked = { navCon.popBackStack() },
                onShopSelected = { scope.launch { ownerViewModel.getShopProducts(it) } },
                onAddProductClicked = { navCon.navigate("AddProductScreen") },
                onFilterChange = { ownerViewModel.updateProductSortType(it) },
                onInfoButtonClick = { clickedProduct ->
                    scope.launch {
                        ownerViewModel.getShopProductDetails(
                            shopProductsState.shopId,
                            clickedProduct
                        )
                        ownerViewModel.updateSelectedProduct(clickedProduct.productId)
                        navCon.navigate("ProductDetailsScreen")
                    }
                }
            )
        }

        composable(route = "ProductDetailsScreen") {
            val shopProductsState by ownerViewModel.shopProductsState.collectAsState()
            val shopId = shopProductsState.shopId

            val product by ownerViewModel.selectedProduct.collectAsState()

            product?.let { p ->
                ProductDetailsScreen(
                    product = ProductIdentity(p.id ?: -1L, p.name, p.company),
                    subProducts = p.subProducts,
                    onBackClicked = {
                        scope.launch {
                            ownerViewModel.updateSelectedProduct(null)
                        }
                        navCon.popBackStack()
                    },
                    onAddSubProductConfirm = { subProductToAdd ->
                        scope.launch {
                            val request = AddSubProductsForShopRequest(
                                subProducts = listOf(
                                    SubProductRequest(
                                        mrp = subProductToAdd.mrp,
                                        sellingUnits = subProductToAdd.sellingUnits.map { unit ->
                                            SellingUnitRequest(
                                                unit.unitType,
                                                unit.packets,
                                                unit.sellingPrice
                                            )
                                        }
                                    )
                                )
                            )
                            val message =
                                ownerViewModel.addShopSubProduct(shopId, p.id ?: -1, request)
                            showToast(context, message)
                        }
                    },
                    onUpdateSellingUnitConfirm = { variant, unit, updates ->
                        scope.launch {
                            val success = ownerViewModel.updateProductSellingUnit(
                                shopId, p.id ?: -1, variant.id ?: -1L, unit.id ?: -1L, updates
                            )
                            if (success) showToast(context, "Selling unit updated successfully")
                        }
                    },
                    onDeleteSubProductConfirm = { variant ->
                        scope.launch {
                            val deleted = ownerViewModel.deleteShopSubProduct(
                                shopId,
                                p.id ?: -1,
                                variant.id ?: -1L
                            )
                            if (deleted) showToast(context, "Variant deleted successfully")
                        }
                    },
                    onDeleteSellingUnitConfirm = { variant, unit ->
                        scope.launch {
                            val deleted = ownerViewModel.deleteProductSellingUnit(
                                shopId, p.id ?: -1, variant.id ?: -1L, unit.id ?: -1L
                            )
                            if (deleted) showToast(context, "Selling unit deleted successfully")
                        }
                    },
                    onDeleteProductConfirm = { productIdentity ->
                        scope.launch {
                            val deleted = ownerViewModel.deleteProduct(productIdentity.productId)
                            if (deleted) {
                                showToast(context, "Product removed successfully")
                                navCon.popBackStack()
                            }
                        }
                    },
                    onAddSellingUnitConfirm = { subProduct, sellingUnitRequest ->
                        scope.launch {
                            val hasUnitAdded = ownerViewModel.addProductSellingUnit(
                                shopId, product?.id ?: -1, subProduct.id ?: -1, sellingUnitRequest
                            )
                            if (hasUnitAdded) showToast(context, "Selling unit added successfully")
                        }
                    }
                )
            }
        }

        composable("AddProductScreen") {
            val state by ownerViewModel.shopProductsState.collectAsState()

            AddProductScreen(
                onBackClicked = { navCon.popBackStack() },
                onSaveProduct = { name, company, subProducts ->
                    scope.launch {
                        val request = AddProductForShopRequest(
                            productName = name,
                            company = company,
                            subProducts = subProducts.map { sub ->
                                SubProductRequest(
                                    mrp = sub.mrp,
                                    sellingUnits = sub.sellingUnits.map { unit ->
                                        SellingUnitRequest(
                                            unit.unitType,
                                            unit.packets,
                                            unit.sellingPrice
                                        )
                                    }
                                )
                            }
                        )
                        val hasAddedProduct = ownerViewModel.addProduct(state.shopId, request)
                        val message =
                            if (hasAddedProduct) "Product added successfully" else "Failed to add product"
                        showToast(context, message)
                        if (hasAddedProduct) navCon.popBackStack()
                    }
                }
            )
        }

        composable(route = "WorkersScreen") {
            val shopsState by ownerViewModel.shopsState.collectAsState()
            val shopIdVsName by remember(shopsState) {
                mutableStateOf(shopsState.shops.associate { it.id to it.name })
            }
            val currentShopIdForWorkers by ownerViewModel.currentShopIdForWorkers.collectAsState()

            WorkersScreen(
                currentShopId = currentShopIdForWorkers,
                shops = shopIdVsName,
                onBackClicked = { navCon.popBackStack() },
                onAddClicked = { navCon.navigate("WorkerAddUpdateScreen_add") },
                onWorkerCardClick = { workerId ->
                    scope.launch {
                        ownerViewModel.updateSelectedWorker(workerId)
                        navCon.navigate("WorkerDetailsScreen")
                    }
                },
                getWorkersForShop = { shopId -> ownerViewModel.getShopWorkers(shopId) },
                onCurrentShopIdForWorkersChange = { ownerViewModel.setCurrentShopIdForWorkers(it) }
            )
        }

        composable(route = "WorkerDetailsScreen") {
            val worker by ownerViewModel.selectedShopWorker.collectAsState()

            WorkerDetailsScreen(
                worker = worker ?: Worker(),
                onBackClicked = {
                    scope.launch {
                        ownerViewModel.updateSelectedWorker(null)
                        navCon.popBackStack()
                    }
                },
                onEditClicked = { navCon.navigate("WorkerAddUpdateScreen_update") },
                onDeleteClicked = {
                    scope.launch {
                        val hasWorkerDeleted = ownerViewModel.deleteShopWorker(worker?.id ?: -1)
                        val message =
                            if (hasWorkerDeleted) "Worker deleted successfully" else "Unable to delete the worker"
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        ownerViewModel.updateSelectedWorker(null)
                        navCon.popBackStack()
                    }
                }
            )
        }

        composable(route = "WorkerAddUpdateScreen_add") {
            val shopId = ownerViewModel.currentShopIdForWorkers.collectAsState().value ?: -1
            WorkerAddUpdateScreen(
                worker = null,
                shopId = shopId,
                onBackClicked = { navCon.popBackStack() },
                onSaveClicked = {
                    scope.launch {
                        if (ownerViewModel.addShopWorker(it)) navCon.popBackStack()
                    }
                }
            )
        }

        composable(route = "WorkerAddUpdateScreen_update") {
            val shopId = ownerViewModel.currentShopIdForWorkers.collectAsState().value ?: -1
            val worker by ownerViewModel.selectedShopWorker.collectAsState()
            WorkerAddUpdateScreen(
                worker = worker,
                shopId = shopId,
                onBackClicked = { navCon.popBackStack() },
                onSaveClicked = {
                    scope.launch {
                        if (ownerViewModel.updateShopWorker(it)) navCon.popBackStack()
                    }
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OwnerHomeScreen(
    navController: NavHostController,
    refreshStats: () -> Unit,
    ownerName: String,
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
                        color = Color(0xFF007BFF)
                    )
                )
                IconButton(onClick = { navController.navigate("OwnerInfoScreen") }) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
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
            Box(modifier = Modifier.fillMaxWidth()) {
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
                text = "Today's Status",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))

            val navigationViewModel =
                if (!getIsPreview()) koinViewModel<NavigationViewModel>(viewModelStoreOwner = getViewModelStoreOwner()) else NullNavigationViewModel()
            val ownerViewModel =
                if (!getIsPreview()) koinViewModel<OwnerViewModel>(viewModelStoreOwner = getViewModelStoreOwner()) else NullOwnerViewModel()

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(getOwnerStats(homeScreenDetails)) { stat ->
                    StatCard(stat, navigationViewModel, ownerViewModel)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatCard(stat: OwnerStat, viewModel: NavigationViewModel?, ownerViewModel: OwnerViewModel?) {
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

                    "Total Shops" -> navController?.navigate("ShopsScreen")
                }
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = if (stat.isHighlighted) Color(0xFF007BFF) else Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
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
        }
    }
}

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
                        "Home" -> navController.navigate("OwnerHomeScreen")
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

data class OwnerStat(val title: String, val value: String, val isHighlighted: Boolean = false)

fun getOwnerStats(details: HomeScreenDetails?): List<OwnerStat> = listOf(
    OwnerStat("Daily Revenue", "₹${details?.salesAmount ?: 0.0}", isHighlighted = true),
    OwnerStat("Active Orders", "${details?.creatingOrderCount ?: 0}"),
    OwnerStat("Total Shops", "${details?.shopCount ?: 0}"),
    OwnerStat("Workers", "${details?.workerCount ?: 0}")
)

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
