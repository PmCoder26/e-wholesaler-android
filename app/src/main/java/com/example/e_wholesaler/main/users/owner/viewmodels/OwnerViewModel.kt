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
import com.example.e_wholesaler.main.users.owner.dtos.ProductRemoveRequest
import com.example.e_wholesaler.main.users.owner.dtos.QuantityToSellingPrice
import com.example.e_wholesaler.main.users.owner.dtos.Shop
import com.example.e_wholesaler.main.users.owner.dtos.SubProduct
import com.example.e_wholesaler.main.users.owner.dtos.SubProductAddRequest
import com.example.e_wholesaler.main.users.owner.dtos.SubProductRemoveRequest
import com.example.e_wholesaler.main.users.owner.dtos.SubProductUpdateRequest
import com.example.e_wholesaler.main.users.owner.dtos.Worker
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
    private var currentShopIdForProducts = MutableStateFlow(shopList.value.firstOrNull()?.id)
    private var shopProductsUpdateTrigger = MutableStateFlow(0)
    val shopProductsState = combine(
        currentShopIdForProducts,
        productSortType,
        shopProductsUpdateTrigger
    ) { currentId, sortType, trigger ->
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

    private var shopIdVsWorkers = mutableMapOf<Long, MutableList<Worker>>()


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
                currentShopIdForProducts.value = shop.id
            }
        }
    }

    fun updateProductSortType(sortType: ProductSortType) {
        viewModelScope.launch(Dispatchers.Main) { productSortType.value = sortType }
    }

    fun getProductByName(productName: String): Product {
        return shopIdVsProducts[currentShopIdForProducts.value]?.find { it.name == productName }
            ?: Product()
    }

    suspend fun removeShopSubProduct(productName: String, subProduct: SubProduct): Boolean =
        withContext(Dispatchers.IO) {
            ifNotNull(ownerId.value, currentShopIdForProducts.value) { ownerId, shopId ->
                val requestDTO = SubProductRemoveRequest(subProduct.id, shopId)
                val response = ownerClient.removeShopSubProduct(ownerId, requestDTO)
                val hasProductRemoved = response?.message == "Shop sub-product removed successfully"

                if (hasProductRemoved) {
                    // 1. Get the current list of products for the shop.
                    val currentProductsForShop = shopIdVsProducts[shopId] ?: return@ifNotNull false
                    // 2. Find the index of the specific product we need to update.
                    val productIndex =
                        currentProductsForShop.indexOfFirst { it.name == productName }
                    if (productIndex == -1) return@ifNotNull false
                    val productToUpdate = currentProductsForShop[productIndex]
                    // 3. Create a NEW list of sub-products with the item removed.
                    val newSubProductsList =
                        productToUpdate.shopSubProducts.filterNot { it.id == subProduct.id }
                    productToUpdate.shopSubProducts = newSubProductsList
                }

                return@ifNotNull hasProductRemoved
            } ?: return@withContext false
        }

    suspend fun addShopSubProduct(productName: String, subProduct: SubProduct): Boolean =
        withContext(Dispatchers.IO) {
            ifNotNull(ownerId.value, currentShopIdForProducts.value) { ownerId, shopId ->
                val mrpToSellingMap = mapOf(
                    subProduct.mrp to QuantityToSellingPrice(
                        subProduct.quantity, subProduct.sellingPrice,
                        subProduct.stock
                    )
                )
                val productToUpdate = getProductByName(productName)
                val requestDTO = SubProductAddRequest(
                    productToUpdate.name, productToUpdate.category,
                    productToUpdate.company, mrpToSellingMap, shopId
                )

                val response = ownerClient.addShopSubProduct(ownerId, requestDTO)
                val hasAddedSubProduct = response?.idToPriceMap.isNullOrEmpty().not()

                if (hasAddedSubProduct) {
                    val subProductId = response.idToPriceMap.keys.toList()[0]
                    val newSubProduct = subProduct.copy(id = subProductId)
                    val newSubProductList = productToUpdate.shopSubProducts.toList() + newSubProduct
                    productToUpdate.shopSubProducts = newSubProductList
                }

                return@ifNotNull hasAddedSubProduct
            } ?: return@withContext false
        }

    suspend fun updateShopSubProduct(productName: String, subProduct: SubProduct): Boolean =
        withContext(Dispatchers.IO) {
            ifNotNull(ownerId.value, currentShopIdForProducts.value) { ownerId, shopId ->
                val requestDTO = SubProductUpdateRequest(
                    subProduct.id, subProduct.mrp, subProduct.sellingPrice,
                    subProduct.quantity, subProduct.stock, shopId
                )

                val response = ownerClient.updateShopSubProduct(ownerId, requestDTO)
                val hasUpdatedSubProduct =
                    response?.message == "Shop's sub-product updated successfully"

                if (hasUpdatedSubProduct) {
                    val productToUpdate = getProductByName(productName)
                    val newSubProductList = productToUpdate.shopSubProducts.map {
                        if (it.id == subProduct.id) subProduct.copy() else it
                    }
                    productToUpdate.shopSubProducts = newSubProductList
                }

                return@ifNotNull hasUpdatedSubProduct
            } ?: return@withContext false
        }

    suspend fun addProduct(product: Product): Boolean =
        withContext(Dispatchers.IO) {
            ifNotNull(ownerId.value, currentShopIdForProducts.value) { ownerId, shopId ->
                val productExists =
                    shopIdVsProducts[shopId]?.any { it.name == product.name } == true
                if (productExists) return@withContext false
                val mrpToSellingMap = product.shopSubProducts.associate { sb ->
                    sb.mrp to QuantityToSellingPrice(sb.quantity, sb.sellingPrice, sb.stock)
                }
                val requestDTO = SubProductAddRequest(
                    product.name, product.category,
                    product.company, mrpToSellingMap, shopId
                )

                // using the same api for adding products and sub-products
                val response = ownerClient.addShopSubProduct(ownerId, requestDTO)
                if (response == null) return@withContext false

                val updatedSubProducts = product.shopSubProducts.mapNotNull { sub ->
                    val createdSubProduct =
                        response.idToPriceMap.entries.find { it.value == sub.sellingPrice }
                            ?: return@mapNotNull null
                    sub.copy(id = createdSubProduct.key, sellingPrice = createdSubProduct.value)
                }
                val newProduct = product.copy(shopSubProducts = updatedSubProducts)
                shopIdVsProducts[shopId] =
                    ((shopIdVsProducts[shopId] ?: emptyList()) + newProduct) as MutableList
                shopProductsUpdateTrigger.value++

                true
            } ?: false
        }

    suspend fun removeProduct(product: Product): Boolean =
        withContext(Dispatchers.IO) {
            ifNotNull(ownerId.value, currentShopIdForProducts.value) { ownerId, shopId ->
                val productExists =
                    shopIdVsProducts[shopId]?.any { it.name == product.name } == true
                if (!productExists) return@withContext false

                val requestDTO = ProductRemoveRequest(shopId, product.name)
                val response = ownerClient.removeProduct(ownerId, requestDTO)

                if (response != null && response.message.contains("Product deleted successfully")) {
                    shopIdVsProducts[shopId]?.remove(product)
                    shopProductsUpdateTrigger.value++
                    return@withContext true
                }

                false
            } ?: false
        }

    suspend fun getShopWorkers(shopId: Long): List<Worker> =
        withContext(Dispatchers.IO) {
            val ownerId = ownerId.value ?: return@withContext emptyList()

            return@withContext shopIdVsWorkers[shopId] ?: run {
                val response =
                    ownerClient.getShopWorkers(ownerId, shopId) ?: return@withContext emptyList()
                shopIdVsWorkers[response.shopId] = response.workerList.toMutableList()

                response.workerList
            }
        }

    private inline fun <T1, T2, R> ifNotNull(a: T1?, b: T2?, block: (a: T1, b: T2) -> R): R? {
        return if (a != null && b != null) block(a, b) else null
    }

}

@RequiresApi(Build.VERSION_CODES.O)
class NullOwnerViewModel() : OwnerViewModel(null!!, null!!)

private fun List<DailyShopRevenue>.getTotalRevenue(): Double {
    var totalRevenue = 0.0
    this.forEach { totalRevenue += it.dailyRevenue }
    return totalRevenue
}