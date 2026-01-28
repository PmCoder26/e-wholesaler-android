package com.example.e_wholesaler.main.users.owner.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_wholesaler.main.users.owner.clients.OwnerClient
import com.example.e_wholesaler.main.users.owner.dtos.AddProductForShopRequest
import com.example.e_wholesaler.main.users.owner.dtos.AddSubProductsForShopRequest
import com.example.e_wholesaler.main.users.owner.dtos.DailyShopRevenue
import com.example.e_wholesaler.main.users.owner.dtos.HomeScreenDetails
import com.example.e_wholesaler.main.users.owner.dtos.OwnerDetails
import com.example.e_wholesaler.main.users.owner.dtos.Product2
import com.example.e_wholesaler.main.users.owner.dtos.ProductIdentity
import com.example.e_wholesaler.main.users.owner.dtos.SellingUnitRequest
import com.example.e_wholesaler.main.users.owner.dtos.SellingUnitUpdate
import com.example.e_wholesaler.main.users.owner.dtos.Shop
import com.example.e_wholesaler.main.users.owner.dtos.Worker
import com.example.e_wholesaler.main.users.owner.dtos.WorkerDeleteRequest
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
import kotlinx.coroutines.flow.asStateFlow
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
        TotalShopRevenue(emptyList(), 0.0, ShopSortType.REVENUE)
    )
    val totalRevenue = combine(revenueDetails, shopRevenueSortType) { revenueDetails, sortType ->
        val sortedList = when (sortType) {
            ShopSortType.REVENUE -> revenueDetails.dailyShopRevenueList.sortedByDescending { it.dailyRevenue }
            ShopSortType.NAME -> revenueDetails.dailyShopRevenueList.sortedBy { it.shopName }
            ShopSortType.CITY -> revenueDetails.dailyShopRevenueList.sortedBy { it.city }
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
    private var shopIdVsProducts = mutableMapOf<Long, MutableList<ProductIdentity>>()
    private var currentShopIdForProducts = MutableStateFlow<Long?>(null)
    private var shopProductsUpdateTrigger = MutableStateFlow(0)

    val shopProductsState = combine(
        currentShopIdForProducts,
        productSortType,
        shopProductsUpdateTrigger,
        shopList
    ) { currentId, sortType, _, shops ->
        if (currentId != null) {
            val list = shopIdVsProducts[currentId] ?: emptyList()
            val sortedList = when (sortType) {
                ProductSortType.NAME -> list.sortedBy { it.productName }
                ProductSortType.COMPANY -> list.sortedBy { it.companyName }
            }
            ShopProductsState(
                shopId = currentId,
                currentShop = shops.find { it.id == currentId } ?: Shop(),
                products = sortedList,
                sortType = sortType
            )
        } else {
            ShopProductsState()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ShopProductsState())

    private var productIdVsProduct = mutableMapOf<Long, Product2>()
    private var _selectedProduct = MutableStateFlow<Product2?>(null)
    val selectedProduct = _selectedProduct.asStateFlow()

    private var shopIdVsWorkers = mutableMapOf<Long, MutableList<Worker>>()
    private var _currentShopIdForWorkers = MutableStateFlow<Long?>(null)
    val currentShopIdForWorkers = _currentShopIdForWorkers.asStateFlow()

    private var _selectedShopWorker = MutableStateFlow<Worker?>(null)
    val selectedShopWorker = _selectedShopWorker.asStateFlow()

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
            shopListResponse?.let {
                shopList.value = it
                if (currentShopIdForProducts.value == null) {
                    currentShopIdForProducts.value = it.firstOrNull()?.id
                }
            }
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
                    val newShopList = shopList.value.toMutableList()
                    newShopList.add(addedShop.formatDateAndGet())
                    shopList.value = newShopList
                    return@withContext true
                }
            }
            return@withContext false
        }
    }

    suspend fun getShopProducts(shop: Shop) {
        withContext(Dispatchers.IO) {
            ownerId.value?.let { ownerId ->
                if (!shopIdVsProducts.containsKey(shop.id)) {
                    val productsResponse =
                        ownerClient.getShopProducts(ownerId, shop.id)?.toMutableList()
                    productsResponse?.let { response -> shopIdVsProducts.put(shop.id, response) }
                }
                currentShopIdForProducts.value = shop.id
                shopProductsUpdateTrigger.value++
            }
        }
    }

    fun updateProductSortType(sortType: ProductSortType) {
        viewModelScope.launch(Dispatchers.Main) { productSortType.value = sortType }
    }

    suspend fun addProduct(shopId: Long, request: AddProductForShopRequest): Boolean =
        withContext(Dispatchers.IO) {
            ownerId.value?.let { ownerId ->
                val response = ownerClient.addProduct(ownerId, shopId, request)
                if (response != null) {
                    val product = response.product
                    val productId = product.id ?: -1L
                    val currentProducts = shopIdVsProducts[shopId] ?: mutableListOf()
                    currentProducts.add(ProductIdentity(productId, product.name, product.company))
                    shopIdVsProducts[shopId] = currentProducts
                    if (productId != -1L) productIdVsProduct[productId] = product
                    shopProductsUpdateTrigger.value++
                    return@withContext true
                }
            }
            return@withContext false
        }

    suspend fun deleteProduct(productId: Long): Boolean =
        withContext(Dispatchers.IO) {
            val shopId = currentShopIdForProducts.value ?: return@withContext false
            ownerId.value?.let { ownerId ->
                val success = ownerClient.deleteProduct(ownerId, shopId, productId)
                if (success) {
                    shopIdVsProducts[shopId]?.removeIf { it.productId == productId }
                    productIdVsProduct.remove(productId)
                    if (_selectedProduct.value?.id == productId) _selectedProduct.value = null
                    shopProductsUpdateTrigger.value++
                }
                return@withContext success
            } ?: false
        }

    suspend fun getShopProductDetails(shopId: Long, productIdentity: ProductIdentity) =
        withContext(Dispatchers.IO) {
            ownerId.value?.let { ownerId ->
                if (productIdVsProduct[productIdentity.productId] == null) {
                    val subProducts = ownerClient.getShopProductDetails(
                        ownerId,
                        shopId,
                        productIdentity.productId
                    )
                    if (subProducts != null) {
                        val product = Product2(
                            id = productIdentity.productId,
                            name = productIdentity.productName,
                            company = productIdentity.companyName,
                            subProducts = subProducts
                        )
                        productIdVsProduct[productIdentity.productId] = product
                    }
                }
                withContext(Dispatchers.Main) {
                    _selectedProduct.value = productIdVsProduct[productIdentity.productId]
                }
            }
        }

    fun updateSelectedProduct(productId: Long?) {
        _selectedProduct.value = if (productId != null) productIdVsProduct[productId] else null
    }

    suspend fun addShopSubProduct(
        shopId: Long,
        productId: Long,
        request: AddSubProductsForShopRequest
    ): String =
        withContext(Dispatchers.IO) {
            ownerId.value?.let { ownerId ->
                val responseData =
                    ownerClient.addShopSubProduct(ownerId, shopId, productId, request)
                if (responseData != null) {
                    if (responseData.addedSubProducts?.isNotEmpty() == true) {
                        val existingProduct = productIdVsProduct[productId]
                        val updatedProduct = existingProduct?.copy(
                            subProducts = (existingProduct.subProducts + responseData.addedSubProducts)
                        )
                        if (updatedProduct != null) {
                            productIdVsProduct[productId] = updatedProduct
                            withContext(Dispatchers.Main) {
                                _selectedProduct.value = updatedProduct
                            }
                        }
                    }
                    return@let responseData.message
                }
                "Unable to add this variant"
            } ?: "Unable to add this variant"
        }

    suspend fun deleteShopSubProduct(
        shopId: Long,
        productId: Long,
        shopSubProductId: Long
    ): Boolean =
        withContext(Dispatchers.IO) {
            ownerId.value?.let { ownerId ->
                val success = ownerClient.deleteShopSubProduct(ownerId, shopId, shopSubProductId)
                if (success) {
                    val existingProduct = productIdVsProduct[productId]
                    if (existingProduct != null) {
                        val newList =
                            existingProduct.subProducts.filterNot { it.id == shopSubProductId }
                        val updatedProduct = existingProduct.copy(subProducts = newList)
                        productIdVsProduct[productId] = updatedProduct
                        withContext(Dispatchers.Main) {
                            _selectedProduct.value = updatedProduct
                        }
                    }
                }
                return@withContext success
            } ?: false
        }


    suspend fun addProductSellingUnit(
        shopId: Long,
        productId: Long,
        shopSubProductId: Long,
        request: SellingUnitRequest
    ): Boolean = withContext(Dispatchers.IO) {
        ownerId.value?.let { ownerId ->
            val addedUnit =
                ownerClient.addProductSellingUnit(ownerId, shopId, shopSubProductId, request)
            if (addedUnit != null) {
                val existingProduct = productIdVsProduct[productId]
                if (existingProduct != null) {
                    val updatedSubProducts = existingProduct.subProducts.map { sub ->
                        if (sub.id == shopSubProductId) {
                            sub.copy(sellingUnits = sub.sellingUnits + addedUnit)
                        } else sub
                    }
                    val updatedProduct = existingProduct.copy(subProducts = updatedSubProducts)
                    productIdVsProduct[productId] = updatedProduct
                    withContext(Dispatchers.Main) {
                        _selectedProduct.value = updatedProduct
                    }
                    return@let true
                }
            }
            return@let false
            } ?: false
    }

    suspend fun updateProductSellingUnit(
        shopId: Long,
        productId: Long,
        shopSubProductId: Long,
        sellingUnitId: Long,
        request: SellingUnitUpdate
    ): Boolean = withContext(Dispatchers.IO) {
        ownerId.value?.let { ownerId ->
            val updatedUnit = ownerClient.updateProductSellingUnit(
                ownerId,
                shopId,
                shopSubProductId,
                sellingUnitId,
                request
            )
            if (updatedUnit != null) {
                val existingProduct = productIdVsProduct[productId]
                if (existingProduct != null) {
                    val updatedSubProducts = existingProduct.subProducts.map { sub ->
                        if (sub.id == shopSubProductId) {
                            sub.copy(sellingUnits = sub.sellingUnits.map { if (it.id == sellingUnitId) updatedUnit else it })
                        } else sub
                    }
                    val updatedProduct = existingProduct.copy(subProducts = updatedSubProducts)
                    productIdVsProduct[productId] = updatedProduct
                    withContext(Dispatchers.Main) {
                        _selectedProduct.value = updatedProduct
                    }
                }
                return@let true
            }
            false
        } ?: false
    }

    suspend fun deleteProductSellingUnit(
        shopId: Long,
        productId: Long,
        shopSubProductId: Long,
        sellingUnitId: Long
    ): Boolean =
        withContext(Dispatchers.IO) {
            ownerId.value?.let { ownerId ->
                val success = ownerClient.deleteProductSellingUnit(
                    ownerId,
                    shopId,
                    shopSubProductId,
                    sellingUnitId
                )
                if (success) {
                    val existingProduct = productIdVsProduct[productId]
                    if (existingProduct != null) {
                        val updatedSubProducts = existingProduct.subProducts.map { sub ->
                            if (sub.id == shopSubProductId) {
                                sub.copy(sellingUnits = sub.sellingUnits.filterNot { it.id == sellingUnitId })
                            } else sub
                        }
                        val updatedProduct = existingProduct.copy(subProducts = updatedSubProducts)
                        productIdVsProduct[productId] = updatedProduct
                        withContext(Dispatchers.Main) {
                            _selectedProduct.value = updatedProduct
                        }
                    }
                }
                return@withContext success
            } ?: false
        }

    suspend fun getShopWorkers(shopId: Long): List<Worker> = withContext(Dispatchers.IO) {
        ownerId.value?.let { ownerId ->
            return@withContext shopIdVsWorkers[shopId] ?: run {
                val response =
                    ownerClient.getShopWorkers(ownerId, shopId) ?: return@run emptyList<Worker>()
                shopIdVsWorkers[response.shopId] = response.workerList.toMutableList()
                response.workerList
            }
        } ?: emptyList()
    }

    fun updateSelectedWorker(workerId: Long?) {
        _selectedShopWorker.value =
            if (workerId != null) shopIdVsWorkers[_currentShopIdForWorkers.value]?.find { it.id == workerId } else null
    }

    fun setCurrentShopIdForWorkers(shopId: Long) {
        _currentShopIdForWorkers.value = shopId
    }

    suspend fun addShopWorker(worker: Worker): Boolean = withContext(Dispatchers.IO) {
        ifNotNull(ownerId.value, _currentShopIdForWorkers.value) { ownerId, shopId ->
            val added = ownerClient.addShopWorker(ownerId, worker) ?: return@ifNotNull false
            val list = shopIdVsWorkers[shopId] ?: mutableListOf()
            list.add(added); shopIdVsWorkers[shopId] = list
            true
        } ?: false
    }

    suspend fun updateShopWorker(worker: Worker): Boolean = withContext(Dispatchers.IO) {
        ifNotNull(ownerId.value, _currentShopIdForWorkers.value) { ownerId, shopId ->
            val updated = ownerClient.updateShopWorker(ownerId, worker) ?: return@ifNotNull false
            _selectedShopWorker.value = updated
            val list = shopIdVsWorkers[shopId] ?: mutableListOf()
            val index = list.indexOfFirst { it.id == worker.id }
            if (index != -1) {
                list[index] = updated; shopIdVsWorkers[shopId] = list; return@ifNotNull true
            }
            false
        } ?: false
    }

    suspend fun deleteShopWorker(workerId: Long): Boolean = withContext(Dispatchers.IO) {
        ifNotNull(ownerId.value, _currentShopIdForWorkers.value) { ownerId, shopId ->
            val success =
                ownerClient.deleteShopWorker(ownerId, WorkerDeleteRequest(workerId, shopId)) != null
            if (success) {
                shopIdVsWorkers[shopId]?.removeIf { it.id == workerId }
            }
            return@ifNotNull success
        } ?: false
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
