package com.example.e_wholesaler.main.users.owner.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_wholesaler.main.users.owner.clients.OwnerClient
import com.example.e_wholesaler.main.users.owner.dtos.DailyShopRevenue
import com.example.e_wholesaler.main.users.owner.dtos.HomeScreenDetails
import com.example.e_wholesaler.main.users.owner.dtos.OwnerDetails
import com.example.e_wholesaler.main.users.owner.dtos.Product
import com.example.e_wholesaler.main.users.owner.dtos.Shop
import com.example.e_wholesaler.main.users.owner.dtos.ShopSubProductRemoveRequest
import com.example.e_wholesaler.main.users.owner.dtos.SubProduct
import com.example.e_wholesaler.main.users.owner.dtos.formatDateAndGet
import com.example.e_wholesaler.main.users.owner.viewmodels.utils.Details
import com.example.e_wholesaler.main.users.owner.viewmodels.utils.ProductSortType
import com.example.e_wholesaler.main.users.owner.viewmodels.utils.ShopProductsState
import com.example.e_wholesaler.main.users.owner.viewmodels.utils.ShopSortType
import com.example.e_wholesaler.main.users.owner.viewmodels.utils.ShopsState
import com.example.e_wholesaler.main.users.owner.viewmodels.utils.TotalShopRevenue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
open class OwnerViewModel(
    private val ownerClient: OwnerClient,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val OWNER_ID_KEY = longPreferencesKey("user_type_id")

    private var ownerId = MutableStateFlow<Long?>(null)


    init {
        viewModelScope.launch {
            // .first() gives the current snapshot of preferences instead of continuous updates.
            val pref = dataStore.data.first()
            ownerId.value = pref[OWNER_ID_KEY]
            refreshHomeScreen()
        }
    }


    private var ownerDetails = MutableStateFlow<OwnerDetails?>(null)
    private var homeDetails = MutableStateFlow<HomeScreenDetails?>(null)
    val detailsFlow = combine(homeDetails, ownerDetails) { homeDetails, ownerDetails ->
        Details(homeDetails, ownerDetails)
    }

    private var shopRevenueSortType = MutableStateFlow(ShopSortType.REVENUE)
    private var revenueDetails = MutableStateFlow(
        TotalShopRevenue(
            emptyList(), 0.0, ShopSortType.REVENUE
        )
    )
    val totalRevenue = combine(revenueDetails, shopRevenueSortType) { revenueDetails, sortType ->
        val sortedList = when (sortType) {
            ShopSortType.REVENUE -> revenueDetails.dailyShopRevenueList.sortedByDescending { it -> it.dailyRevenue }
            ShopSortType.NAME -> revenueDetails.dailyShopRevenueList.sortedBy { it -> it.shopName }
            ShopSortType.CITY -> revenueDetails.dailyShopRevenueList.sortedBy { it -> it.city }
        }
        TotalShopRevenue(
            totalRevenue = revenueDetails.totalRevenue,
            dailyShopRevenueList = sortedList,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private var shopSortType = MutableStateFlow(ShopSortType.NAME)
    private var shopList = MutableStateFlow(emptyList<Shop>())
    val shopsState = combine(shopList, shopSortType) { shopList, shopSortType ->
        val sortedList = when (shopSortType) {
            ShopSortType.CITY -> shopList.sortedBy { it.city }
            else -> shopList.sortedBy { it.name }
        }
        ShopsState(shops = sortedList, sortType = shopSortType)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ShopsState())

    private var productSortType = MutableStateFlow(ProductSortType.NAME)
    private var shopIdVsProducts = mutableMapOf<Long, MutableList<Product>>()
    private var currentShopId = MutableStateFlow(shopList.value.firstOrNull()?.id)
    val shopProductsState = combine(currentShopId, productSortType) { currentId, sortType ->
        if (currentId != null) {
            val list = shopIdVsProducts[currentId] ?: emptyList()
            val sortedList = when (sortType) {
                ProductSortType.NAME -> list.sortedBy { it.name }
                ProductSortType.CATEGORY -> list.sortedBy { it.category }
                ProductSortType.COMPANY -> list.sortedBy { it.company }
            }
            ShopProductsState(currentId, sortedList, sortType)
        } else {
            ShopProductsState()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ShopProductsState())


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun refreshHomeScreen() {
        getHomeScreenDetails()
        getOwnerDetails()
        getOwnerShops()
    }

    suspend fun getHomeScreenDetails() = withContext(Dispatchers.IO) {
        ownerId.value?.let {
            val homeScreenDetails = ownerClient.getHomeScreenDetails(it)
            homeDetails.value = homeScreenDetails
        }
    }

    private suspend fun getOwnerDetails() = withContext(Dispatchers.IO) {
        ownerId.value?.let {
            val ownerDetails = ownerClient.getOwnerDetails(it)
            this@OwnerViewModel.ownerDetails.value = ownerDetails
        }
    }

    fun getDailyRevenue() {
        viewModelScope.launch(Dispatchers.IO) {
            ownerId.value?.let {
                val dailyShopRevenueList = ownerClient.getDailyRevenue(it)
                dailyShopRevenueList?.let {
                    revenueDetails.value = TotalShopRevenue(
                        dailyShopRevenueList = dailyShopRevenueList,
                        totalRevenue = dailyShopRevenueList.getTotalRevenue(),
                        sortType = shopRevenueSortType.value
                    )
                }
            }
        }
    }

    fun updateShopRevenueSortType(sortType: ShopSortType) {
        viewModelScope.launch(Dispatchers.Main) {
            shopRevenueSortType.value = sortType
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getOwnerShops() = withContext(Dispatchers.IO) {
        ownerId.value?.let {
            val shopListResponse = ownerClient.getOwnerShops(it)
                ?.map { it.formatDateAndGet() }
            shopListResponse?.let { shopList.value = it }
        }
    }

    fun updateShopSortType(sortType: ShopSortType) {
        viewModelScope.launch(Dispatchers.Main) {
            shopSortType.value = sortType
        }
    }

    fun getShopById(shopId: Long): Shop {
        return shopList.value.find { it.id == shopId } ?: Shop()
    }

    fun getCurrentShopId() = shopProductsState.value.shopId

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateShopDetails(shop: Shop): Boolean {
        return withContext(Dispatchers.IO) {
            ownerId.value?.let { ownerId ->
                val updatedShop = ownerClient.updateShopDetails(ownerId, shop)
                updatedShop?.let { updatedShop ->
                    val newShopList = shopList.value
                        .map {
                            if (it.id == shop.id) updatedShop.formatDateAndGet() else it
                        }
                    shopList.value = newShopList
                    return@withContext true
                }
            }
            return@withContext false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addNewShop(newShop: Shop): Boolean {
        return withContext(Dispatchers.IO) {
            ownerId.value?.let { ownerId ->
                val addedShop = ownerClient.addNewShop(ownerId, newShop)
                addedShop?.let { addedShop ->
                    val newShopList = shopList.value
                        .toMutableList()
                    newShopList.add(addedShop.formatDateAndGet())
                    shopList.value = newShopList
                    return@withContext true
                }
            }
            return@withContext false
        }
    }

    fun getShopProducts(shop: Shop) {
        viewModelScope.launch(Dispatchers.IO) {
            ownerId.value?.let { ownerId ->
                if (!shopIdVsProducts.containsKey(shop.id)) {
                    val productsResponse = ownerClient.getShopProducts(ownerId, shop.id)
                    productsResponse?.let { response -> shopIdVsProducts.put(shop.id, response) }
                }
                currentShopId.value = shop.id
            }
        }
    }

    fun updateProductSortType(sortType: ProductSortType) {
        viewModelScope.launch(Dispatchers.Main) { productSortType.value = sortType }
    }

    fun getProductByName(productName: String): Product {
        return shopIdVsProducts[currentShopId.value]?.find { it.name == productName }?.copy()
            ?: Product()
    }

    suspend fun removeShopSubProduct(productName: String, subProduct: SubProduct): Boolean =
        withContext(Dispatchers.IO) {
            val ownerId = ownerId.value ?: return@withContext false
            val currentShopIdVal = getCurrentShopId() // Get current shop ID
            val requestDTO = ShopSubProductRemoveRequest(subProduct.id, currentShopIdVal)
            val removeProductResponse = ownerClient.removeShopSubProduct(ownerId, requestDTO)
            val hasProductRemoved =
                removeProductResponse?.message == "Shop sub-product removed successfully"

            if (hasProductRemoved) {
                // 1. Get the current list of products for the shop.
                val currentProductsForShop =
                    shopIdVsProducts[currentShopIdVal] ?: return@withContext false
                // 2. Find the index of the specific product we need to update.
                val productIndex = currentProductsForShop.indexOfFirst { it.name == productName }
                if (productIndex == -1) return@withContext false
                val productToUpdate = currentProductsForShop[productIndex]
                // 3. Create a NEW list of sub-products with the item removed.
                val newSubProductsList =
                    productToUpdate.shopSubProducts.filterNot { it.id == subProduct.id }
                productToUpdate.shopSubProducts = newSubProductsList
            }
            return@withContext hasProductRemoved
        }

}

@RequiresApi(Build.VERSION_CODES.O)
class NullOwnerViewModel() : OwnerViewModel(null!!, null!!)

private fun List<DailyShopRevenue>.getTotalRevenue(): Double {
    var totalRevenue = 0.0
    this.forEach { totalRevenue += it.dailyRevenue }
    return totalRevenue
}